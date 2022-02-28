package com.ey.batch.processor;

import com.ey.batch.dto.InputDto;
import com.ey.batch.dto.OutputDto;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class InputItemProcessor implements ItemProcessor<InputDto, OutputDto> {

    @Override
    public OutputDto process(InputDto inputDto) {
        return new OutputDto(inputDto.getFirstName() + " " + inputDto.getLastName());
    }
}
