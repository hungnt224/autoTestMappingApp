package framework;

import java.util.List;

public class Models {
    public static class TestCase {
        public String name;
        public List<Step> steps;
    }

    public static class Step {
        public String action, target, value, expected;
    }
}