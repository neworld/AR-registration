LOCAL_PATH := $(call my-dir)
 
include $(CLEAR_VARS)
 
LOCAL_MODULE    := process
LOCAL_SRC_FILES := process.c
 
include $(BUILD_SHARED_LIBRARY)