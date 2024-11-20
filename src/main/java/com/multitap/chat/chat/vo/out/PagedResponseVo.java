package com.multitap.chat.chat.vo.out;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponseVo {

    private Page<ChatResponseVo> content;      // 현재 페이지의 데이터
    private boolean hasNext;

    public static PagedResponseVo of(Page<ChatResponseVo> content, boolean hasNext) {
        return PagedResponseVo.builder()
                .content(content)
                .hasNext(hasNext)
                .build();
    }
}
