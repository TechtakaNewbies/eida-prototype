package org.outofoffice.eida.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.outofoffice.lib.core.ui.EidaEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrolment implements EidaEntity<String> {
    private String enrollmentId;
    private Student student;
    private Subject subject;

    @Override
    public String getId() {
        return enrollmentId;
    }

    @Override
    public void setId(String s) {
        enrollmentId = s;
    }
}
