package com.proj.sac.util;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageStructure 
{
	private String to;
	private String subject;
	private Date sentDate;
	private String text;
}
