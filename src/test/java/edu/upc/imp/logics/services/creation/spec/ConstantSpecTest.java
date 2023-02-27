package edu.upc.imp.logics.services.creation.spec;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConstantSpecTest {

    @Test
    public void should_beAbleToCreateAConstantSpec() {
        ConstantSpec constantSpec = new ConstantSpec("a");

        assertThat(constantSpec.getName()).isEqualTo("a");
    }

}