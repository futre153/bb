package org.pabk.web;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

public class ScrapEntries extends ArrayList<Hashtable<String, Object>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	//filtering
	private static final int FILTER_MASK						= 0x0007;
	private static final int TIME_FILTER_MASK	 				= 0x0001;
	private static final int MOBILE_NO_FILTER_MASK 				= 0x0002;
	private static final int AMOUNT_FILTER_MASK 				= 0x0004;
	//primary sorting
	private static final int PRIMARY_SORTING_MASK				= 0x0070;
	private static final int PRIMARY_SORTING_ASC_MASK			= 0x0008;
	private static final int PRIMARY_ID_SORTING_MASK	 		= 0x0010;
	private static final int PRIMARY_SENT_SORTING_MASK	 		= 0x0020;
	private static final int PRIMARY_DELIVERED_SORTING_MASK		= 0x0040;

	private static final String NO_MASK = "(\\+\\d+)|(\\d+)";
	public static final String ISO_CURRENCY_MASK = "[A-Z]{3}";
	public static final String NUMERIC_CURRENCY_MASK = "[0-9]+";
	
	private int flag = 0;
	//time filter
	private long minTime = -1;
	private long maxTime = -1;
	//Mobile number filter
	private String[] numbers = {};
	//amount filter
	private int currency = -1;
	private long minAmt = -1;
	private long maxAmt = -1;
	
	private long timestamp = -1;
	
	ScrapEntries(Properties prop) {
		setFlag(prop);
	}
	
	

	public boolean add (Hashtable<String, Object> value) {
		boolean retValue = ((getFlag() & FILTER_MASK) > 0) ? applyFilter(value) : true;
		if(retValue) {
			super.add(applySorting(value), value);
		}
		this.setTimestamp(System.currentTimeMillis());
		return retValue;
	}

	private int applySorting(Hashtable<String, Object> value) {
		switch (this.getFlag() & PRIMARY_SORTING_MASK) {
		case PRIMARY_ID_SORTING_MASK:
			return sort (value, PRIMARY_SORTING_ASC_MASK > 0, PrepAppClientServlet.INTERNAL_ID_KEY);
		case PRIMARY_SENT_SORTING_MASK:
			return sort (value, PRIMARY_SORTING_ASC_MASK > 0, PrepAppClientServlet.EXECUTIONTIME_KEY);
		case PRIMARY_DELIVERED_SORTING_MASK:
			return sort (value, PRIMARY_SORTING_ASC_MASK > 0, PrepAppClientServlet.RECEPTIONTIME_KEY);
		default:
			return this.size();
		}
	}


	
	
	
	private int sort (Hashtable<String, Object> value, boolean asc, String key) {
		Object id1 = value.get(key);
		int i = asc ? 0 : this.size();
		if(asc) {
			for(; i < this.size(); i ++) {
				Object id2 = this.get(i).get(key);
				if(id2 == null) {
					continue;
				}
				else {
					if(id1 == null) {
						continue;
					}
					if (id1 instanceof Date) {
						if (((Date) id1).getTime() < ((Date) id2).getTime()) {
							break;
						}
					}
					if (id1 instanceof Long) {
						if (((long) id1) < ((long) id2)) {
							break;
						}
					}

				}
			}
		}
		else {
			for(; i > 0; i --) {
				Object id2 = this.get(i).get(key);
				if(id1 == null) {
					continue;
				}
				else {
					if(id2 == null) {
						continue;
					}
					if (id1 instanceof Date) {
						if (((Date) id1).getTime() > ((Date) id2).getTime()) {
							break;
						}
					}
					if (id1 instanceof Long) {
						if (((long) id1) > ((long) id2)) {
							break;
						}
					}
				}
			}
		}
		return i;
	}

	
	
	

	private boolean applyFilter(Hashtable<String, Object> value) {
		boolean retValue = true;
		if((this.getFlag() & TIME_FILTER_MASK) > 0) {
			retValue = filterByTime(value);
		}
		if(retValue && ((this.getFlag() & MOBILE_NO_FILTER_MASK) > 0)) {
			retValue = filterByMobileNo(value);
		}
		if(retValue && ((this.getFlag() & AMOUNT_FILTER_MASK) > 0)) {
			retValue = filterByAmouunt(value);
		}
		return retValue;
	}



	private boolean filterByAmouunt(Hashtable<String, Object> value) {
		boolean retValue = false;
		long cur = -1;
		Object v = value.get(PrepAppClientServlet.PAYMENT_CCY_KEY);
		if(v == null) {
			v = value.get(PrepAppClientServlet.TRANSACTION_CURRENCY_KEY);
		}
		if(this.getCurrency() < 0) {
			retValue = true;
		}
		else {
			if(v == null) {
				retValue = false;
			}
			else {
				if(((String) v).matches(ISO_CURRENCY_MASK)) {
					cur = Long.parseLong((String) v);
				}
				else if (((String) v).matches(NUMERIC_CURRENCY_MASK)) {
					Iterator<Currency> iter = Currency.getAvailableCurrencies().iterator();
					while (iter.hasNext()) {
						Currency curr = iter.next();
						if (curr.getCurrencyCode().equalsIgnoreCase((String) v)) {
							cur = curr.getNumericCode();
							break;
						}
					}
				}
				retValue = (cur == this.getCurrency());
			}
		}
		if(retValue) {
			v = value.get(PrepAppClientServlet.TRANSACTION_AMOUNT_KEY);
			if(v == null) {
				v = value.get(PrepAppClientServlet.PAYMENT_AMT_KEY);
			}
			long min = this.getMinTime() < 0 ? 0 : this.getMinAmt();
			long max = this.getMaxTime() < 0 ? Long.MAX_VALUE : this.getMaxAmt();
			if(v == null) {
				retValue = false;
			}
			else {
				long amt = (long) v;
				retValue = (amt >= min) && (amt <= max);
			}
			return retValue;
		}
		return retValue;
	}



	private boolean filterByMobileNo(Hashtable<String, Object> value) {
		boolean retValue = false;
		Object v = value.get(PrepAppClientServlet.CONTACT_KEY);
		if(v == null) {
			retValue = false;
		}
		else {
			String contact = ((String) v);
			String[] no = this.getNumbers();
			if(no != null) {
				for(int i = 0; i < no.length; i ++) {
					if((retValue = contact.contains(no[i]))) {
						break;
					}
				}
				
			}
			else {
				retValue = true;
			}
		}
		return retValue;
	}



	private boolean filterByTime(Hashtable<String, Object> value) {
		boolean retValue = true;
		Object v = value.get(PrepAppClientServlet.EXECUTIONTIME_KEY);
		long min = this.getMinTime() < 0 ? 0 : this.getMinTime();
		long max = this.getMaxTime() < 0 ? Long.MAX_VALUE : this.getMinTime();
		if(v == null) {
			retValue = false;
		}
		else {
			long time = (long) v;
			retValue = (time >= min) && (time <= max);
		}
		if(!retValue) {
			v = value.get(PrepAppClientServlet.RECEPTIONTIME_KEY);
			if(v == null) {
				retValue = false;
			}
			else {
				long time = (long) v;
				retValue = (time >= min) && (time <= max);
			}
		}
		return retValue;
	}



	private final int getFlag() {
		return flag;
	}

	private final void setFlag(Properties prop) {
		try {
			this.setTimestamp(System.currentTimeMillis());
			this.flag = Integer.parseInt(prop.getProperty(PrepAppClientServlet.FILTER_FLAG_KEY), 2);
			switch (this.getFlag() & FILTER_MASK) {
			case TIME_FILTER_MASK:
				this.setMinTime(Long.parseLong(prop.getProperty(PrepAppClientServlet.TIME_FILTER_MIN_KEY)));
				this.setMaxTime(Long.parseLong(prop.getProperty(PrepAppClientServlet.TIME_FILTER_MAX_KEY)));
				break;
			case MOBILE_NO_FILTER_MASK:
				this.setNumbers(prop.getProperty(prop.getProperty(PrepAppClientServlet.MOBILE_NO_FILTER_LIST_KEY)).split(prop.getProperty(PrepAppClientServlet.SEPARATOR_KEY)));
				break;
			case AMOUNT_FILTER_MASK:
				this.setMinTime(Long.parseLong(prop.getProperty(PrepAppClientServlet.AMOUNT_FILTER_MIN_KEY)));
				this.setMaxTime(Long.parseLong(prop.getProperty(PrepAppClientServlet.AMOUNT_FILTER_MAX_KEY)));
				this.setCurrency(prop.getProperty(PrepAppClientServlet.AMOUNT_FILTER_CURRENCY_KEY));
				break;
			default:
				flag = flag & PRIMARY_SORTING_MASK;
			}
			
		}
		catch(Exception e) {
			reset();
		}
	}

	private void reset() {
		this.flag = 0;
		this.setMinAmt(-1);
		this.setMaxAmt(-1);
		this.setMinTime(-1);
		this.setMaxTime(-1);
		this.setCurrency("");
		this.setNumbers();
	}

	private final long getMinTime() {
		return minTime;
	}

	private final void setMinTime(long minTime) {
		this.minTime = minTime;
	}

	private final long getMaxTime() {
		return maxTime;
	}

	private final void setMaxTime(long maxTime) {
		this.maxTime = maxTime;
	}

	private final String[] getNumbers() {
		return numbers;
	}

	private final void setNumbers(String ... numbers) {
		
		String[] tmp = new String[PrepAppClientServlet.MAX_NUMBERS];
		int index = 0;
		for(int i = 0; i < tmp.length; i ++) {
			try {
				tmp[index] = parseMobileNo(numbers[i]);
				index ++;
			}
			catch (Exception e) {
				
			}
			finally {
				if(index >= tmp.length) {
					break;
				}
			}
		}
		this.numbers = new String[index];
		System.arraycopy(tmp, 0, numbers, 0, index);
		this.numbers = numbers;
	}

	private static String parseMobileNo(String no) {
		no = no.replaceAll(" ", "");
		if(no.matches(NO_MASK)) {
			return no;
		}
		throw new NumberFormatException ();
	}

	private final int getCurrency() {
		return currency;
	}

	private final void setCurrency(String currency) {
		Iterator<Currency> cur;
		this.currency = -1;
		if(currency.toUpperCase().matches(ISO_CURRENCY_MASK)) {
			cur = Currency.getAvailableCurrencies().iterator();
			while (cur.hasNext()) {
				Currency c = cur.next();
				if(c.getCurrencyCode().equalsIgnoreCase(currency)) {
					this.currency = c.getNumericCode();
					break;
				}
			}
		}
		else if (currency.matches(NUMERIC_CURRENCY_MASK)) {
			cur = Currency.getAvailableCurrencies().iterator();
			int i = Integer.parseInt(currency);
			while (cur.hasNext()) {
				Currency c = cur.next();
				if(c.getNumericCode() == i) {
					this.currency = c.getNumericCode();
					break;
				}
			}
		}
	}

	private final long getMinAmt() {
		return minAmt;
	}

	private final void setMinAmt(long minAmt) {
		this.minAmt = minAmt;
	}

	private final long getMaxAmt() {
		return maxAmt;
	}

	private final void setMaxAmt(long maxAmt) {
		this.maxAmt = maxAmt;
	}



	final long getTimestamp() {
		return timestamp;
	}



	private final void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}



	
	
}
