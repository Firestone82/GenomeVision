package cz.mik0486.semestralproject.data.holder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@AllArgsConstructor
public class Sample {

    private String name;
    private Matrix matrix2D;

    @Override
    public String toString() {
        return name;
    }
}
