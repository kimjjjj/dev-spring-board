package hello.dev.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Block {

    private Integer seq;
    private String userId; // ID
    private String blockId; // 차단한 ID
    private String insDt; // 입력 일시
}
