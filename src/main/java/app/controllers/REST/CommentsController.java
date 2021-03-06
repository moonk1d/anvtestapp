package app.controllers.REST;

import app.ExceptionHandler.exceptions.FieldIsRequiredException;
import app.models.Comment;
import app.models.Post;
import app.models.User;
import app.services.interfaces.CommentService;
import app.services.interfaces.PostService;
import app.services.interfaces.UserService;
import app.validators.QueryParametersValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

/**
 * Created by Andrey Nazarov on 7/27/2018.
 */
@RestController
@RequestMapping(value = "api/comments")
public class CommentsController extends MainController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "{id}", produces = "application/json")
    public Comment viewComment(@PathVariable("id") String id) {
        QueryParametersValidator.validateIdQueryParameter(id);

        return commentService.findById(Long.valueOf(id));
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<Comment>> listComments(
            @RequestParam(value = "limit", required = false) String limit,
            @RequestParam(value = "offset", required = false) String offset) {

        Pageable page = pageRequestBuilder(offset, limit);

        return new ResponseEntity<>(commentService.findAll(page), HttpStatus.OK);
    }

    @GetMapping(value = "users/{userid}/posts/{postid}", produces = "application/json")
    public ResponseEntity<List<Comment>> listCommentsByUserByPost(
            @PathVariable("userid") String userId,
            @PathVariable("postid") String postId,
            @RequestParam(value = "limit", required = false) String limit,
            @RequestParam(value = "offset", required = false) String offset) {

        Pageable page = pageRequestBuilder(offset, limit);

        Post post = postService.findById(Long.valueOf(postId));
        User author = userService.findById(Long.valueOf(userId));

        return new ResponseEntity<>(commentService.findByAuthorAndPost(post, author, page), HttpStatus.OK);
    }

    @GetMapping(value = "users/{userid}", produces = "application/json")
    public ResponseEntity<List<Comment>> listCommentsByUser(
            @PathVariable("userid") String userId,
            @RequestParam(value = "limit", required = false) String limit,
            @RequestParam(value = "offset", required = false) String offset) {

        Pageable page = pageRequestBuilder(offset, limit);

        User author = userService.findById(Long.valueOf(userId));

        return new ResponseEntity<>(commentService.findByAuthor(author, page), HttpStatus.OK);
    }

    @GetMapping(value = "posts/{postid}", produces = "application/json")
    public ResponseEntity<List<Comment>> listCommentsByPost(
            @PathVariable("postid") String postId,
            @RequestParam(value = "limit", required = false) String limit,
            @RequestParam(value = "offset", required = false) String offset) {

        Pageable page = pageRequestBuilder(offset, limit);

        Post post = postService.findById(Long.valueOf(postId));

        return new ResponseEntity<>(commentService.findByPost(post, page), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity createComment(@RequestBody Comment comment) {
        checkRequiredFieldsForComment(comment);

        Post post = postService.findById(comment.getPost().getId());
        User author = userService.findById(comment.getAuthor().getId());

        comment.setPost(post);
        comment.setAuthor(author);

        return ResponseEntity
                .created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(commentService.create(comment).getId())
                        .toUri())
                .build();
    }

    @PatchMapping(value = "{id}")
    public ResponseEntity updateComment(@PathVariable("id") String id, @RequestBody Comment comment) {
        QueryParametersValidator.validateIdQueryParameter(id);
        Comment updatedComment = commentService.findById(Long.valueOf(id));

        if (comment.getContent() != null) {
            updatedComment.setContent(comment.getContent());
        }

        commentService.create(updatedComment);

        return ResponseEntity
                .created(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .build()
                        .toUri())
                .build();
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity deleteComment(@PathVariable("id") String id) {
        QueryParametersValidator.validateIdQueryParameter(id);

        Comment comment = commentService.findById(Long.valueOf(id));

        commentService.deleteById(comment.getId());

        return ResponseEntity.noContent().build();
    }

    public void checkRequiredFieldsForComment(Comment comment) {
        Long postId = comment.getPost().getId();
        Long userId = comment.getAuthor().getId();

        if (postId == null) {
            throw new FieldIsRequiredException("'post id' is required.");
        }

        if (userId == null) {
            throw new FieldIsRequiredException("'author id' is required.");
        }
    }
}