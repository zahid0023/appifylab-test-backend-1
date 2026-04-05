# System Design — Posts, Comments, Replies, Likes & Dislikes

## Table of Contents
1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Database Schema](#database-schema)
4. [Entity Relationships](#entity-relationships)
5. [Domain Design](#domain-design)
6. [API Reference](#api-reference)
7. [Key Design Decisions](#key-design-decisions)

---

## Overview

This module implements a social content system supporting:
- **Posts** with optional images
- **Comments** on posts with infinite nested replies (self-referencing)
- **Reactions** (like / dislike) on both posts and comments

All content supports soft deletion, optimistic locking, and full audit trails via a shared `AuditableEntity` base class.

---

## Architecture

```
HTTP Request
     │
     ▼
┌─────────────┐
│  Controller  │  ← validates input (@Valid), resolves current user
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Service   │  ← business logic, ownership checks, transaction management
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Repository  │  ← Spring Data JPA, derived queries + JPQL
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  PostgreSQL │
└─────────────┘
```

### Layer responsibilities

| Layer | Responsibility |
|---|---|
| Controller | Input validation, authentication context, HTTP status codes |
| Service | Business rules, ownership enforcement, transaction boundaries |
| Repository | Data access, filtering by `isActive`/`isDeleted`, pagination |
| Mapper | Entity ↔ DTO conversion, no business logic |

---

## Database Schema

### `posts`
| Column | Type | Constraints | Default |
|---|---|---|---|
| `id` | bigserial | PK | — |
| `user_id` | bigint | NOT NULL, FK → users CASCADE | — |
| `content` | text | nullable | — |
| `is_public` | boolean | NOT NULL | `true` |
| `created_by` | bigint | NOT NULL | — |
| `created_at` | timestamptz | NOT NULL | `CURRENT_TIMESTAMP` |
| `updated_by` | bigint | NOT NULL | — |
| `updated_at` | timestamptz | NOT NULL | `CURRENT_TIMESTAMP` |
| `version` | bigint | NOT NULL | `0` |
| `is_active` | boolean | NOT NULL | `true` |
| `is_deleted` | boolean | NOT NULL | `false` |
| `deleted_by` | bigint | nullable | — |
| `deleted_at` | timestamptz | nullable | — |

---

### `post_images`
| Column | Type | Constraints | Default |
|---|---|---|---|
| `id` | bigserial | PK | — |
| `post_id` | bigint | NOT NULL, FK → posts CASCADE | — |
| `image_url` | text | NOT NULL | — |
| `caption` | text | nullable | — |
| `is_default` | boolean | NOT NULL | `false` |
| *(+ audit columns)* | | | |

---

### `post_likes`
| Column | Type | Constraints | Default |
|---|---|---|---|
| `id` | bigserial | PK | — |
| `post_id` | bigint | NOT NULL, FK → posts CASCADE | — |
| `user_id` | bigint | NOT NULL, FK → users CASCADE | — |
| `is_like` | boolean | NOT NULL | `true` |
| *(+ audit columns)* | | | |
| **UNIQUE** | | `(post_id, user_id)` | — |

---

### `comments`
| Column | Type | Constraints | Default |
|---|---|---|---|
| `id` | bigserial | PK | — |
| `post_id` | bigint | NOT NULL, FK → posts CASCADE | — |
| `user_id` | bigint | NOT NULL, FK → users CASCADE | — |
| `parent_comment_id` | bigint | nullable, FK → comments CASCADE | — |
| `content` | text | NOT NULL | — |
| *(+ audit columns)* | | | |

> `parent_comment_id = NULL` → top-level comment
> `parent_comment_id = {id}` → reply to that comment (infinitely nestable)

---

### `comment_likes`
| Column | Type | Constraints | Default |
|---|---|---|---|
| `id` | bigserial | PK | — |
| `comment_id` | bigint | NOT NULL, FK → comments CASCADE | — |
| `user_id` | bigint | NOT NULL, FK → users CASCADE | — |
| `is_like` | boolean | NOT NULL | `true` |
| *(+ audit columns)* | | | |
| **UNIQUE** | | `(comment_id, user_id)` | — |

---

## Entity Relationships

```
UserEntity
  │
  ├──< PostEntity (user_id)
  │       │
  │       ├──< PostImageEntity (post_id)        [cascade ALL, orphanRemoval]
  │       ├──< PostLikeEntity (post_id)
  │       └──< CommentEntity (post_id)
  │               │
  │               ├──< CommentEntity (parent_comment_id)   [self-referencing, infinite depth]
  │               └──< CommentLikeEntity (comment_id)
  │
  ├──< PostLikeEntity (user_id)
  └──< CommentLikeEntity (user_id)
```

### Cascade & deletion behaviour

| Relationship | On parent delete |
|---|---|
| User → Posts | CASCADE (DB-level `@OnDelete`) |
| Post → PostImages | CASCADE (JPA `CascadeType.ALL` + `orphanRemoval`) |
| Post → PostLikes | CASCADE (DB-level) |
| Post → Comments | CASCADE (DB-level) |
| Comment → CommentLikes | CASCADE (DB-level) |
| Comment → Replies | CASCADE (DB-level, self-referencing) |

---

## Domain Design

### AuditableEntity (base class)

All entities extend this. Provides:

| Field | Type | Description |
|---|---|---|
| `id` | Long | Auto-generated primary key |
| `createdBy` | Long | User ID who created (0 = system) |
| `createdAt` | Instant | Creation timestamp (auto) |
| `updatedBy` | Long | User ID who last updated (auto) |
| `updatedAt` | Instant | Last update timestamp (auto) |
| `version` | Long | Optimistic locking version |
| `isActive` | Boolean | Active flag (default `true`) |
| `isDeleted` | Boolean | Soft delete flag (default `false`) |
| `deletedBy` | Long | User ID who deleted |
| `deletedAt` | Instant | Deletion timestamp |

---

### Soft Delete Pattern

Records are never physically removed. On deletion:
- `isDeleted = true`
- `isActive = false`
- `deletedBy` and `deletedAt` are set

All repository queries filter with `isActive = true AND isDeleted = false`.

---

### Reaction Model (Like / Dislike)

A single `isLike` boolean field covers both reactions:

| `isLike` value | Meaning |
|---|---|
| `true` | Like |
| `false` | Dislike |
| *(no record)* | No reaction |

**Constraints:**
- One record per `(user, post)` and `(user, comment)` — enforced by DB unique constraint
- Creating a reaction when one exists → `409 CONFLICT`
- Reactions can be switched (like ↔ dislike) via `PUT`
- Reactions can be removed via `DELETE`

---

### Nested Comment Model

Comments use self-referencing via `parent_comment_id`:

```
Post
└── Comment A          (parent_comment_id = NULL)
    ├── Reply A1       (parent_comment_id = A)
    │   └── Reply A1a  (parent_comment_id = A1)
    └── Reply A2       (parent_comment_id = A)
└── Comment B          (parent_comment_id = NULL)
```

- Top-level comments: `GET /api/v1/posts/{post-id}/comments`
- Replies to any comment: `GET /api/v1/posts/{post-id}/comments/{comment-id}/replies`
- Creating a reply: `POST /api/v1/posts/{post-id}/comments` with `parent_comment_id` in body
- Depth is unlimited — the client controls how deep to fetch

---

### Image Management

- Each post can have multiple images
- One image can be flagged as `is_default = true` (cover image)
- Images have an optional `caption`
- When a post is updated with a new `images` array, all existing images are **replaced** (clear + re-insert via `orphanRemoval = true`)
- If `images` is omitted in an update request, existing images are left untouched

---

## API Reference

### Posts

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/posts` | Required | Create a post |
| `GET` | `/api/v1/posts` | Required | List all posts (paginated) |
| `GET` | `/api/v1/posts/me` | Required | List my posts (paginated) |
| `GET` | `/api/v1/posts/{id}` | Required | Get post by ID |
| `PUT` | `/api/v1/posts/{id}` | Required (owner) | Update post |
| `DELETE` | `/api/v1/posts/{id}` | Required (owner) | Delete post (soft) |

### Post Reactions

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/posts/{post-id}/likes` | Required | Add reaction |
| `GET` | `/api/v1/posts/{post-id}/likes` | Required | List reactions (paginated) |
| `GET` | `/api/v1/posts/{post-id}/likes/summary` | Required | Like/dislike counts + my reaction |
| `PUT` | `/api/v1/posts/{post-id}/likes` | Required | Update reaction |
| `DELETE` | `/api/v1/posts/{post-id}/likes` | Required | Remove reaction |

### Comments & Replies

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/posts/{post-id}/comments` | Required | Create comment or reply |
| `GET` | `/api/v1/posts/{post-id}/comments` | Required | List top-level comments (paginated) |
| `GET` | `/api/v1/posts/{post-id}/comments/{comment-id}` | Required | Get comment by ID |
| `GET` | `/api/v1/posts/{post-id}/comments/{comment-id}/replies` | Required | List replies (paginated) |
| `PUT` | `/api/v1/posts/{post-id}/comments/{comment-id}` | Required (owner) | Update comment |
| `DELETE` | `/api/v1/posts/{post-id}/comments/{comment-id}` | Required (owner) | Delete comment (soft) |

### Comment Reactions

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/comments/{comment-id}/likes` | Required | Add reaction |
| `GET` | `/api/v1/comments/{comment-id}/likes` | Required | List reactions (paginated) |
| `GET` | `/api/v1/comments/{comment-id}/likes/summary` | Required | Like/dislike counts + my reaction |
| `PUT` | `/api/v1/comments/{comment-id}/likes` | Required | Update reaction |
| `DELETE` | `/api/v1/comments/{comment-id}/likes` | Required | Remove reaction |

### Pagination parameters (all list endpoints)

| Parameter | Type | Default | Constraints | Allowed sort fields |
|---|---|---|---|---|
| `page` | int | `0` | `>= 0` | — |
| `size` | int | `10` | `1–50` | — |
| `sort_by` | string | `id` | — | `id`, `createdAt`, `updatedAt` |
| `sort_dir` | string | `ASC` | `ASC`, `DESC` | — |

### Common response shapes

**SuccessResponse**
```json
{ "success": true, "id": 42 }
```

**ErrorResponse**
```json
{
  "request_id": "abc-123",
  "status": 404,
  "error": "ENTITY_NOT_FOUND",
  "message": "Post not found with id: 42"
}
```

**PaginatedResponse**
```json
{
  "data": [...],
  "current_page": 0,
  "total_pages": 5,
  "total_elements": 48,
  "page_size": 10,
  "has_next": true,
  "has_previous": false
}
```

### Error codes

| HTTP | Code | Cause |
|---|---|---|
| `400` | `INVALID_ARGUMENT` | Validation failure or invalid sort field |
| `401` | — | Missing or expired JWT token |
| `403` | `FORBIDDEN` | Attempting to modify another user's content |
| `404` | `ENTITY_NOT_FOUND` | Post, comment, or reaction not found |
| `409` | `CONFLICT` | Duplicate reaction (already reacted) |
| `409` | `DATA_INTEGRITY_VIOLATION` | DB unique constraint violation |
| `500` | `INTERNAL_SERVER_ERROR` | Unexpected server error |

---

## Key Design Decisions

### 1. Single `isLike` boolean instead of separate like/dislike tables
Simplifies the schema and queries. One record per user per content item with a boolean distinguishing the reaction type. Switching reactions is a single `UPDATE`.

### 2. Self-referencing comments for infinite nesting
Rather than a separate `replies` table, `CommentEntity` references itself via `parent_comment_id`. This supports unlimited depth with no schema changes. The API exposes depth one level at a time (fetch replies per comment) to avoid unbounded data loading.

### 3. Soft delete over hard delete
All deletions set `isDeleted = true` rather than removing rows. This preserves audit history and allows potential recovery. All queries explicitly filter `isDeleted = false`.

### 4. `orphanRemoval = true` on post images
When updating a post's image list, clearing the collection and adding new entries automatically deletes orphaned image records via JPA — no manual delete queries needed.

### 5. Ownership enforced at the service layer
Update and delete operations look up records by both ID and `userEntity` in the same query (`findByIdAndUserEntityAndIsActiveAndIsDeleted`). If the record doesn't belong to the current user it simply returns empty → `404`, avoiding leaking whether the resource exists.

### 6. `replyCount` computed per comment on read
Rather than storing a counter column, `replyCount` is computed with a `COUNT` query per comment during mapping. Simple and always consistent. For high-traffic scenarios this could be moved to a cached counter column.

### 7. Optimistic locking via `@Version`
The `version` field on `AuditableEntity` prevents lost updates when concurrent requests modify the same record. JPA automatically increments it on every update and throws `OptimisticLockException` on conflict.
