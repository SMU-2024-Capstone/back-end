package capstone.courseweb.ai;
/*
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CsvPlaceWriter implements ItemWriter<AIPlaceDto> {

    private final PlaceRepository placeRepository;

    @Override
    public void write(Chunk<? extends AIPlaceDto> chunk) throws Exception {
        List<Place> placeList = new ArrayList<>();

        chunk.forEach(getAIPlaceDto -> {
            Place place = getAIPlaceDto.toEntity();
            placeList.add(place);
        });

        placeRepository.saveAll(placeList);
    }
}


 */