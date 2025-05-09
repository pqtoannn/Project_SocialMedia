package com.project.social_media.controllers;

import com.project.social_media.application.DTO.CommentDTO;
import com.project.social_media.application.Exception.ResourceNotFound;
import com.project.social_media.application.IService.IAuthenticationService;
import com.project.social_media.application.IService.ICommentService;
import com.project.social_media.application.IService.IPostService;
import com.project.social_media.application.IService.IUserService;
import com.project.social_media.controllers.ApiResponse.ApiResponse;
import com.project.social_media.controllers.Request.Comment.CommentCreateRequest;
import com.project.social_media.controllers.Request.Comment.CommentUpdateRequest;
import com.project.social_media.domain.Model.JPA.Comment;
import com.project.social_media.domain.Model.JPA.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/comments")
public class CommentController {
    private final ICommentService commentService;
    private final IUserService  userService;
    private final IPostService postService;
    private final IAuthenticationService authenticationService;

    /*
     * GET Method
     *
     * All the GET method will be available to public guest.
     *
     * If guest request an action (like create, delete, update Post, Comment, Like... etc.),
     * Will be redirected to front-end login page for authentication
     */

    /**
     * <h1>GET: Get All Comments</h1>
     * <h5>URL: api/v1/comments/all</h5>
     * <br>
     *
     * <li>Retrieves all comments in the system</li>
     *
     * @return {@link ApiResponse} containing a list of {@link CommentDTO}
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllComment() {
        try {
            List<Comment> commentList = commentService.getAllComments();
            List<CommentDTO> commentDTOList = commentService.convertToDTOList(commentList);
            return ResponseEntity.ok(new ApiResponse("Success", commentDTOList));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error!", e.getMessage()));
        }
    }

    /**
     * <h1>GET: Get All Comments By Post ID</h1>
     * <h5>URL: api/v1/comments/all/post</h5>
     * <br>
     *
     * <li>Retrieves all comments associated with a specific post</li>
     *
     * @param postId The ID of the post whose comments to retrieve
     * @return {@link ApiResponse} containing a list of {@link CommentDTO}
     */
    @GetMapping("/all/post")
    public ResponseEntity<ApiResponse> getAllCommentByPostId(@RequestParam(required = false) Long postId) {
        try {
            List<Comment> commentList = commentService.getAllCommentsByPostId(postId);
            List<CommentDTO> commentDTOList = commentService.convertToDTOList(commentList);
            return ResponseEntity.ok(new ApiResponse("Success", commentDTOList));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error!", e.getMessage()));
        }
    }

    @GetMapping("/all/shared-post")
    public ResponseEntity<ApiResponse> getAllCommentBySharedPostId(@RequestParam(required = false) Long sharedPostId) {
        try {
            List<Comment> commentList = commentService.getAllCommentsBySharedPostId(sharedPostId);
            List<CommentDTO> commentDTOList = commentService.convertToDTOList(commentList);
            return ResponseEntity.ok(new ApiResponse("Success", commentDTOList));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error!", e.getMessage()));
        }
    }

    /**
     * <h1>GET: Get All Comments By User ID</h1>
     * <h5>URL: api/v1/comments/all/user</h5>
     * <br>
     *
     * <li>Retrieves all comments created by a specific user</li>
     *
     * @param userId The ID of the user whose comments to retrieve
     * @return {@link ApiResponse} containing a list of {@link CommentDTO}
     */
    @GetMapping("/all/user")
    public ResponseEntity<ApiResponse> getAllCommentByUserId(@RequestParam(required = false) Long userId) {
        try {
            List<Comment> commentList = commentService.getAllCommentsByUserId(userId);
            List<CommentDTO> commentDTOList = commentService.convertToDTOList(commentList);
            return ResponseEntity.ok(new ApiResponse("Success", commentDTOList));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error!", e.getMessage()));
        }
    }

    /*
     * POST Method
     *
     * Action that required user information will have to authenticate
     * Otherwise will be redirected to log in page for authentication
     */

    /**
     * <h1>POST: Create Comment</h1>
     * <h5>URL: api/v1/comments/create</h5>
     * <br>
     *
     * <li>Creates a new comment on a specific post by the authenticated user</li>
     *
     * @param postId The ID of the post to comment on
     * @param request {@link CommentCreateRequest} containing comment content
     * @return {@link ApiResponse} containing the created {@link CommentDTO}
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createComment(
            @RequestParam(required = false) Long postId,
            @RequestParam(required = false) Long sharedPostId,
            @ModelAttribute CommentCreateRequest request) {
        try {
            // Get authenticate user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            authenticationService.checkValidationAuth(authentication);
            User authUser = userService.getUserByUsername(authentication.getName())
                    .orElseThrow(() -> new ResourceNotFound("getUserByUsername: username not found"));

            // Create comment
            Comment newComment;
            if (postId != null) {
                newComment = commentService.createComment(authUser.getUserId(), postId, request);
            } else if (sharedPostId != null) {
                newComment = commentService.createCommentForSharedPost(authUser.getUserId(), sharedPostId, request);
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Error!", "Either postId or sharedPostId must be provided"));
            }

            CommentDTO newCommentDTO = commentService.convertToDTO(newComment);
            return ResponseEntity.ok(new ApiResponse("Success", newCommentDTO));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error!", e.getMessage()));
        }
    }

    /**
     * <h1>PUT: Update Comment</h1>
     * <h5>URL: api/v1/comments/comment/{commentId}/update</h5>
     * <br>
     * <li>Update an existing comment for a Post or SharedPost</li>
     *
     * @param commentId The ID of the comment to update
     * @param postId    The ID of the Post (optional if sharedPostId is provided)
     * @param sharedPostId The ID of the SharedPost (optional if postId is provided)
     * @param request   The comment update request
     * @return {@link ApiResponse} containing the updated CommentDTO
     */
    @PutMapping("/comment/{commentId}/update")
    public ResponseEntity<ApiResponse> updateComment(
            @PathVariable Long commentId,
            @RequestParam(required = false) Long postId,
            @RequestParam(required = false) Long sharedPostId,
            @ModelAttribute CommentUpdateRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            authenticationService.checkValidationAuth(authentication);
            User authUser = userService.getUserByUsername(authentication.getName())
                    .orElseThrow(() -> new ResourceNotFound("getUserByUsername: username not found"));
            Comment existingComment = commentService.getCommentById(commentId);

            // Authentication Check
            if (!authUser.getUserId().equals(existingComment.getUser().getUserId())) {
                return ResponseEntity.status(UNAUTHORIZED)
                        .body(new ApiResponse("Invalid Permission", null));
            }

            // Update comment
            Comment updatedComment;
            if (postId != null && existingComment.getPost() != null) {
                updatedComment = commentService.updateComment(authUser.getUserId(), postId, commentId, request);
            } else if (sharedPostId != null && existingComment.getSharedPost() != null) {
                updatedComment = commentService.updateCommentForSharedPost(authUser.getUserId(), sharedPostId, commentId, request);
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Error!", "Invalid postId or sharedPostId for this comment"));
            }

            CommentDTO updatedCommentDTO = commentService.convertToDTO(updatedComment);
            return ResponseEntity.ok(new ApiResponse("Success", updatedCommentDTO));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error!", e.getMessage()));
        }
    }

    /*
     * DELETE Method
     *
     * Action that required user information will have to authenticate
     * Otherwise will be redirected to log in page for authentication
     */

    /**
     * <h1>DELETE: Delete Comment</h1>
     * <h5>URL: api/v1/comments/comment/delete</h5>
     * <br>
     *
     * <li>Deletes an existing comment owned by the authenticated user</li>
     * <li>Validates that the current user is the owner of the comment</li>
     *
     * @param commentId The ID of the comment to delete
     * @return {@link ApiResponse} confirming successful deletion
     */
    @DeleteMapping("/comment/delete")
    public ResponseEntity<ApiResponse> deleteComment(@RequestParam Long commentId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            authenticationService.checkValidationAuth(authentication);
            User user = userService.getUserByUsername(authentication.getName()).orElse(null);
            Comment existingComment = commentService.getCommentById(commentId);

            // Authentication Check
            if (!user.getUserId().equals(existingComment.getUser().getUserId())) {
                return ResponseEntity.status(UNAUTHORIZED)
                        .body(new ApiResponse("Invalid Permission", null));
            }

            // Delete
            commentService.deleteCommentById(commentId);
            return ResponseEntity.ok(new ApiResponse("Success", null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error!", e.getMessage()));
        }
    }
}
