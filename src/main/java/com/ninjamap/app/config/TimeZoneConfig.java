package com.ninjamap.app.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import java.util.TimeZone;

@Configuration
public class TimeZoneConfig {

    @PostConstruct
    public void init() {
        //Ensure JVM and Hibernate use Asia/Kolkata (canonical ID)
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        System.setProperty("user.timezone", "Asia/Kolkata");
        System.out.println("✅ Default JVM TimeZone: " + TimeZone.getDefault().getID());
    }
}

