package cz.mik0486.semestralproject.data.holder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

@Data
@Slf4j
@AllArgsConstructor
public class Sample {

    private String name;

    @Nullable
    private Matrix matrix;

    /* ==============================
     *            Methods
     * ==============================
     */

    @Override
    public String toString() {
        return name;
    }

    public boolean isLoaded() {
        return matrix != null;
    }
}
