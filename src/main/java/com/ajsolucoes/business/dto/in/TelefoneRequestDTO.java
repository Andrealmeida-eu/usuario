package com.ajsolucoes.business.dto.in;

import lombok.*;

@Getter
@Setter

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TelefoneRequestDTO {

    private Long id;
    private String numero;
    private String ddd;

}
