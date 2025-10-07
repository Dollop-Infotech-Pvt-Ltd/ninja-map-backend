package com.ninjamap.app.payload.response;

import com.ninjamap.app.enums.InquiryType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactUsResponse {
    private String id ;
    private String fullName;
    private String emailAddress;
    private InquiryType inquiryType;
    private String subject;
    private String message;
}
