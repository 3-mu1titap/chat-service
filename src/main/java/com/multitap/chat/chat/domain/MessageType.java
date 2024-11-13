package com.multitap.chat.chat.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {
    TEXT, MEDIA, FILE, NOTICE;
}
