package net.engineeringdigest.journalApp.response;

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherResponse{
    public Current current;

    @Getter
    @Setter
    public class Current{
        @JsonProperty("last_updated")
        public String lastUpdated;

        @JsonProperty("temp_c")
        public double tempC;

        @JsonProperty("feelslike_c")
        public double feelslikeC;

        public int humidity;

        public int cloud;

    }
}



