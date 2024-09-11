package capstone.courseweb.random.domain;

import lombok.Data;

import java.util.List;

@Data
public class SelectedCategory {
    private String region;
    private List<List<String>> categories;
}
