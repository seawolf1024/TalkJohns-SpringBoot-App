package com.coder.talkjohns.utils;

public interface CommunityConstant {

    /**
     * Activated successfully
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * Activated repeatedly
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * Activate failed
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * Default login expired time
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * Rememberme login expired time
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    /**
     * Entity type: post
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * Entity type: comments
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * Entity type: user
     */
    int ENTITY_TYPE_USER = 3;
}









