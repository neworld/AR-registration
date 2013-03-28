LOCAL_PATH := $(call my-dir)
 
include $(CLEAR_VARS)
 
LOCAL_MODULE    := process
LOCAL_SRC_FILES := process.c
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog 
 
include $(BUILD_SHARED_LIBRARY)