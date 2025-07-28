package com.ajsolucoes.business.dto.out;

import lombok.*;

@Getter
@Setter

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TelefoneResponseDTO {

    private String numero;
    private String ddd;

}
