# API Documentation

## Table of Contents

1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Common Patterns](#common-patterns)
4. [Auth Endpoints](#auth-endpoints)
   - [Login](#post-apiv1authlogin)
   - [Refresh Token](#post-apiv1authrefresh-token)
   - [Logout](#post-apiv1authlogout)
   - [Register User](#post-apiv1authregistrationuser)
   - [Forgot Password](#post-apiv1authforgot-password)
   - [Verify OTP](#post-apiv1authverify-otp)
   - [Reset Password](#post-apiv1authreset-password)
5. [Admin Endpoints](#admin-endpoints)
   - [Create Admin](#post-apiv1admins)
   - [Activate Admin](#put-apiv1adminsadmin-idactivate)
   - [Assign Permissions](#post-apiv1adminsadmin-idpermissions)
6. [Posts](#posts)
   - [Create Post](#post-apiv1posts)
   - [Get All Posts](#get-apiv1posts)
   - [Get My Posts](#get-apiv1postsme)
   - [Get Post by ID](#get-apiv1postsid)
   - [Update Post](#put-apiv1postsid)
   - [Delete Post](#delete-apiv1postsid)
7. [Comments](#comments)
   - [Create Comment](#post-apiv1postspost-idcomments)
   - [Get Post Comments](#get-apiv1postspost-idcomments)
   - [Get Comment by ID](#get-apiv1postspost-idcommentscomment-id)
   - [Get Comment Replies](#get-apiv1postspost-idcommentscomment-idreplies)
   - [Update Comment](#put-apiv1postspost-idcommentscomment-id)
   - [Delete Comment](#delete-apiv1postspost-idcommentscomment-id)
8. [Post Reactions](#post-reactions)
   - [Like/Dislike Post](#post-apiv1postspost-idlikes)
   - [Get Post Reactions](#get-apiv1postspost-idlikes)
   - [Get Post Like Summary](#get-apiv1postspost-idlikessummary)
   - [Update Post Reaction](#put-apiv1postspost-idlikes)
   - [Remove Post Reaction](#delete-apiv1postspost-idlikes)
9. [Comment Reactions](#comment-reactions)
   - [Like/Dislike Comment](#post-apiv1commentscomment-idlikes)
   - [Get Comment Reactions](#get-apiv1commentscomment-idlikes)
   - [Get Comment Like Summary](#get-apiv1commentscomment-idlikessummary)
   - [Update Comment Reaction](#put-apiv1commentscomment-idlikes)
   - [Remove Comment Reaction](#delete-apiv1commentscomment-idlikes)

---

## Overview

**Base URL:** `http://localhost:8080`

**API Version:** v1

**Content-Type:** `application/json`

**JSON Naming:** All request and response fields use `snake_case`.

---

## Authentication

Most endpoints require a JWT Bearer token in the `Authorization` header:

```
Authorization: Bearer <access_token>
```

Access tokens are obtained via the [Login](#post-apiv1authlogin) endpoint. When an access token expires, use [Refresh Token](#post-apiv1authrefresh-token) to obtain a new one without re-authenticating.

### Public Endpoints (no token required)
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh-token`
- `POST /api/v1/auth/registration/user`
- `POST /api/v1/auth/forgot-password`
- `POST /api/v1/auth/verify-otp`
- `POST /api/v1/auth/reset-password`
- `GET /api/v1/posts` (read-only browsing)
- `GET /api/v1/posts/{id}`

---

## Common Patterns

### Pagination

All list endpoints accept the following query parameters:

| Parameter | Type | Default | Constraints | Description |
|---|---|---|---|---|
| `page` | integer | `0` | >= 0 | Page number (zero-indexed) |
| `size` | integer | `10` | 1–50 | Items per page |
| `sort_by` | string | `"id"` | endpoint-specific | Field to sort by |
| `sort_dir` | string | `"ASC"` | `ASC` or `DESC` | Sort direction |

**Paginated Response:**
```json
{
  "data": ["..."],
  "current_page": 0,
  "total_pages": 5,
  "total_elements": 42,
  "page_size": 10,
  "has_next": true,
  "has_previous": false
}
```

### Success Response (mutations)
```json
{
  "success": true,
  "id": 123
}
```

### Error Response
```json
{
  "timestamp": "2026-04-06T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "..."
}
```

---

## Auth Endpoints

### POST `/api/v1/auth/login`

Authenticates a user and returns JWT tokens.

**Auth required:** No

**Request Body:**
```json
{
  "user_name": "john_doe",
  "password": "secret123"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `user_name` | string | Yes | Username or email |
| `password` | string | Yes | User password |

**Response `200 OK`:**
```json
{
  "token_type": "Bearer",
  "access_token": "eyJhbGciOiJIUzI1NiJ9...",
  "refresh_token": "dGhpcyBpcyBhIHJlZnJlc2g..."
}
```

---

### POST `/api/v1/auth/refresh-token`

Rotates the refresh token and returns a new access token.

**Auth required:** No

**Request Body:**
```json
{
  "refresh_token": "dGhpcyBpcyBhIHJlZnJlc2g..."
}
```

**Response `200 OK`:** Same structure as [Login response](#post-apiv1authlogin).

---

### POST `/api/v1/auth/logout`

Revokes all refresh tokens for the authenticated user.

**Auth required:** Yes

**Request Body:** None

**Response `204 No Content`**

---

### POST `/api/v1/auth/registration/user`

Registers a new user account.

**Auth required:** No

**Request Body:**
```json
{
  "first_name": "John",
  "last_name": "Doe",
  "email": "john@example.com",
  "password": "secret123",
  "confirm_password": "secret123"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `first_name` | string | Yes | User's first name |
| `last_name` | string | Yes | User's last name |
| `email` | string | Yes | Email address (used as username) |
| `password` | string | Yes | Password |
| `confirm_password` | string | Yes | Must match `password` |

**Response `201 Created`:** User details.

---

### POST `/api/v1/auth/forgot-password`

Sends a one-time password (OTP) to the user's registered email.

**Auth required:** No

**Request Body:**
```json
{
  "user_name": "john@example.com"
}
```

**Response `200 OK`:** Confirmation message.

---

### POST `/api/v1/auth/verify-otp`

Verifies the OTP and returns a short-lived reset token.

**Auth required:** No

**Request Body:**
```json
{
  "user_name": "john@example.com",
  "otp": "482910"
}
```

**Response `200 OK`:**
```json
{
  "reset_token": "eyJhbGciOi..."
}
```

---

### POST `/api/v1/auth/reset-password`

Resets the user's password using the reset token obtained from OTP verification.

**Auth required:** No

**Request Body:**
```json
{
  "reset_token": "eyJhbGciOi...",
  "new_password": "newSecret456",
  "confirm_password": "newSecret456"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `reset_token` | string | Yes | Token from `/verify-otp` |
| `new_password` | string | Yes | New password |
| `confirm_password` | string | Yes | Must match `new_password` |

**Response `200 OK`:** Confirmation message.

---

## Admin Endpoints

### POST `/api/v1/admins`

Creates a new admin account.

**Auth required:** Yes — requires `CREATE_ADMIN` permission

**Request Body:** Same as [Register User](#post-apiv1authregistrationuser).

**Response `201 Created`:** Admin details.

---

### PUT `/api/v1/admins/{admin-id}/activate`

Activates an admin account.

**Auth required:** Yes — requires `ACTIVATE_ADMIN` permission

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `admin-id` | integer | ID of the admin to activate |

**Request Body:** None

**Response `200 OK`:** Updated admin details.

---

### POST `/api/v1/admins/{admin-id}/permissions`

Assigns permissions to an admin.

**Auth required:** Yes — requires `ASSIGN_PERMISSIONS` permission

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `admin-id` | integer | ID of the admin to grant permissions to |

**Request Body:**
```json
{
  "permission_ids": [1, 2, 3]
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `permission_ids` | array of integers | Yes | IDs of permissions to assign |

**Response `200 OK`:** `"Permissions assigned successfully"`

---

## Posts

### POST `/api/v1/posts`

Creates a new post.

**Auth required:** Yes

**Request Body:**
```json
{
  "content": "Hello world!",
  "is_public": true,
  "images": [
    {
      "image_url": "https://example.com/photo.jpg",
      "caption": "A nice photo",
      "is_default": true
    }
  ]
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `content` | string | Yes | Post text content |
| `is_public` | boolean | Yes | Whether the post is publicly visible |
| `images` | array | No | List of images to attach |
| `images[].image_url` | string | Yes | URL of the image |
| `images[].caption` | string | No | Image caption |
| `images[].is_default` | boolean | Yes | Whether this is the default/cover image |

**Response `201 Created`:**
```json
{
  "data": {
    "id": 1,
    "content": "Hello world!",
    "is_public": true,
    "images": [
      {
        "id": 1,
        "image_url": "https://example.com/photo.jpg",
        "caption": "A nice photo",
        "is_default": true
      }
    ],
    "user_id": 42,
    "created_at": "2026-04-06T10:00:00Z",
    "updated_at": "2026-04-06T10:00:00Z"
  }
}
```

---

### GET `/api/v1/posts`

Returns a paginated list of all public posts.

**Auth required:** No

**Query Parameters:** See [Pagination](#pagination). Allowed `sort_by` values: `id`, `createdAt`, `updatedAt`.

**Response `200 OK`:** [Paginated response](#pagination) with `PostDto` items.

---

### GET `/api/v1/posts/me`

Returns a paginated list of the authenticated user's own posts.

**Auth required:** Yes

**Query Parameters:** See [Pagination](#pagination). Allowed `sort_by` values: `id`, `createdAt`, `updatedAt`.

**Response `200 OK`:** [Paginated response](#pagination) with `PostDto` items.

---

### GET `/api/v1/posts/{id}`

Returns a single post by ID.

**Auth required:** No

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `id` | integer | Post ID |

**Response `200 OK`:**
```json
{
  "data": {
    "id": 1,
    "content": "Hello world!",
    "is_public": true,
    "images": ["..."],
    "user_id": 42,
    "created_at": "2026-04-06T10:00:00Z",
    "updated_at": "2026-04-06T10:00:00Z"
  }
}
```

---

### PUT `/api/v1/posts/{id}`

Updates a post. Only the post owner can update it.

**Auth required:** Yes (must be post owner)

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `id` | integer | Post ID |

**Request Body:** Same as [Create Post](#post-apiv1posts).

**Response `200 OK`:** Updated `PostDto` wrapped in `{ "data": ... }`.

---

### DELETE `/api/v1/posts/{id}`

Deletes a post. Only the post owner can delete it.

**Auth required:** Yes (must be post owner)

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `id` | integer | Post ID |

**Response `200 OK`:**
```json
{
  "success": true,
  "id": 1
}
```

---

## Comments

### POST `/api/v1/posts/{post-id}/comments`

Creates a comment on a post. Set `parent_comment_id` to reply to an existing comment.

**Auth required:** Yes

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `post-id` | integer | ID of the post to comment on |

**Request Body:**
```json
{
  "content": "Great post!",
  "parent_comment_id": null
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `content` | string | Yes | Comment text |
| `parent_comment_id` | integer | No | ID of parent comment for replies; omit for top-level comments |

**Response `201 Created`:**
```json
{
  "data": {
    "id": 10,
    "post_id": 1,
    "user_id": 42,
    "parent_comment_id": null,
    "content": "Great post!",
    "reply_count": 0,
    "created_at": "2026-04-06T10:00:00Z",
    "updated_at": "2026-04-06T10:00:00Z"
  }
}
```

---

### GET `/api/v1/posts/{post-id}/comments`

Returns a paginated list of top-level comments for a post.

**Auth required:** No

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `post-id` | integer | Post ID |

**Query Parameters:** See [Pagination](#pagination). Allowed `sort_by` values: `id`, `createdAt`, `updatedAt`.

**Response `200 OK`:** [Paginated response](#pagination) with `CommentDto` items.

---

### GET `/api/v1/posts/{post-id}/comments/{comment-id}`

Returns a single comment by ID.

**Auth required:** No

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `post-id` | integer | Post ID (for URL scoping) |
| `comment-id` | integer | Comment ID |

**Response `200 OK`:** `{ "data": CommentDto }`.

---

### GET `/api/v1/posts/{post-id}/comments/{comment-id}/replies`

Returns a paginated list of replies to a comment.

**Auth required:** No

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `post-id` | integer | Post ID (for URL scoping) |
| `comment-id` | integer | Parent comment ID |

**Query Parameters:** See [Pagination](#pagination). Allowed `sort_by` values: `id`, `createdAt`, `updatedAt`.

**Response `200 OK`:** [Paginated response](#pagination) with `CommentDto` items.

---

### PUT `/api/v1/posts/{post-id}/comments/{comment-id}`

Updates a comment. Only the comment owner can update it.

**Auth required:** Yes (must be comment owner)

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `post-id` | integer | Post ID (for URL scoping) |
| `comment-id` | integer | Comment ID |

**Request Body:**
```json
{
  "content": "Updated comment text"
}
```

**Response `200 OK`:** `{ "data": CommentDto }`.

---

### DELETE `/api/v1/posts/{post-id}/comments/{comment-id}`

Deletes a comment. Only the comment owner can delete it.

**Auth required:** Yes (must be comment owner)

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `post-id` | integer | Post ID (for URL scoping) |
| `comment-id` | integer | Comment ID |

**Response `200 OK`:**
```json
{
  "success": true,
  "id": 10
}
```

---

## Post Reactions

### POST `/api/v1/posts/{post-id}/likes`

Adds a like or dislike reaction to a post.

**Auth required:** Yes

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `post-id` | integer | Post ID |

**Request Body:**
```json
{
  "is_like": true
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `is_like` | boolean | Yes | `true` = like, `false` = dislike |

**Response `201 Created`:** Reaction details.

---

### GET `/api/v1/posts/{post-id}/likes`

Returns a paginated list of all reactions on a post.

**Auth required:** No

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `post-id` | integer | Post ID |

**Query Parameters:** See [Pagination](#pagination). Allowed `sort_by` values: `id`, `createdAt`.

**Response `200 OK`:** [Paginated response](#pagination) with reaction items.

---

### GET `/api/v1/posts/{post-id}/likes/summary`

Returns the like/dislike summary for a post, including the authenticated user's own reaction.

**Auth required:** Yes

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `post-id` | integer | Post ID |

**Response `200 OK`:**
```json
{
  "data": {
    "like_count": 24,
    "dislike_count": 3,
    "my_reaction": true
  }
}
```

| Field | Type | Description |
|---|---|---|
| `like_count` | integer | Total likes |
| `dislike_count` | integer | Total dislikes |
| `my_reaction` | boolean\|null | `true` = liked, `false` = disliked, `null` = no reaction |

---

### PUT `/api/v1/posts/{post-id}/likes`

Updates the authenticated user's existing reaction on a post.

**Auth required:** Yes

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `post-id` | integer | Post ID |

**Request Body:** Same as [Like/Dislike Post](#post-apiv1postspost-idlikes).

**Response `200 OK`:** Updated reaction details.

---

### DELETE `/api/v1/posts/{post-id}/likes`

Removes the authenticated user's reaction from a post.

**Auth required:** Yes

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `post-id` | integer | Post ID |

**Response `200 OK`:**
```json
{
  "success": true,
  "id": 1
}
```

---

## Comment Reactions

### POST `/api/v1/comments/{comment-id}/likes`

Adds a like or dislike reaction to a comment.

**Auth required:** Yes

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `comment-id` | integer | Comment ID |

**Request Body:**
```json
{
  "is_like": true
}
```

**Response `201 Created`:** Reaction details.

---

### GET `/api/v1/comments/{comment-id}/likes`

Returns a paginated list of all reactions on a comment.

**Auth required:** No

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `comment-id` | integer | Comment ID |

**Query Parameters:** See [Pagination](#pagination). Allowed `sort_by` values: `id`, `createdAt`.

**Response `200 OK`:** [Paginated response](#pagination) with reaction items.

---

### GET `/api/v1/comments/{comment-id}/likes/summary`

Returns the like/dislike summary for a comment, including the authenticated user's own reaction.

**Auth required:** Yes

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `comment-id` | integer | Comment ID |

**Response `200 OK`:**
```json
{
  "data": {
    "like_count": 5,
    "dislike_count": 1,
    "my_reaction": null
  }
}
```

---

### PUT `/api/v1/comments/{comment-id}/likes`

Updates the authenticated user's existing reaction on a comment.

**Auth required:** Yes

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `comment-id` | integer | Comment ID |

**Request Body:** Same as [Like/Dislike Comment](#post-apiv1commentscomment-idlikes).

**Response `200 OK`:** Updated reaction details.

---

### DELETE `/api/v1/comments/{comment-id}/likes`

Removes the authenticated user's reaction from a comment.

**Auth required:** Yes

**Path Parameters:**

| Parameter | Type | Description |
|---|---|---|
| `comment-id` | integer | Comment ID |

**Response `200 OK`:**
```json
{
  "success": true,
  "id": 1
}
```
