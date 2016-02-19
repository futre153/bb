package org.pabk.basen.rfc;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.pabk.basen.asn1.ASN1Impl;
import org.pabk.basen.asn1.Choice;
import org.pabk.basen.asn1.GeneralizedTime;
import org.pabk.basen.asn1.UTCTime;
import org.pabk.basen.ber.BERImpl;

public class Time extends Choice {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String UTC_TIME_NAME = "utcTime";
	private static final boolean UTC_TIME_OPT = false;
	private static final String GEN_TIME_NAME = "generalTime";
	private static final boolean GEN_TIME_OPT = false;
	private static final String UTC_TIME_MASK = "yyMMddHHmmssZ";
	private static final SimpleDateFormat sdf = new SimpleDateFormat(UTC_TIME_MASK);
	private final ASN1Impl[] TIME_SEQ = {
		new UTCTime(UTC_TIME_NAME, UTC_TIME_OPT),
		new GeneralizedTime(GEN_TIME_NAME, GEN_TIME_OPT)
	};

	public Time(String name, boolean optional) {
		super(name, optional);
		this.setSequences(TIME_SEQ);
	}
	private Time() {}
	@Override
	public Time clone() {
		return (Time) ASN1Impl.clone(new Time(), this);
	}
	
	public static BERImpl createUTCTime (long date) throws IOException {
		BERImpl utc = new BERImpl (BERImpl.UTC_TIME_TAG, BERImpl.PRIMITIVE_ENCODING);
		System.out.println(sdf.format(new Date(date)));
		utc.getEncoder().setValue(utc, sdf.format(new Date(date)).getBytes());
		return utc;
	}
	
	
}
