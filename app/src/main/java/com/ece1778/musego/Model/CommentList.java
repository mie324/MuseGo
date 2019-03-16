package com.ece1778.musego.Model;

import java.util.List;

public class CommentList {

    private List<Comment> commentList;

    public CommentList(List<Comment> commentList){
        this.commentList = commentList;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }
}
