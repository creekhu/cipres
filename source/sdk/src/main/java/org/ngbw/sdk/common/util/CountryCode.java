package org.ngbw.sdk.common.util;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import org.ngbw.sdk.WorkbenchException;

/*
	Terri: I got the dictionary of countrycode/countryname from bootstrap form helpers.
	Our restusers registration application uses the javascript bootstrap form helper for a country
	code selection control.
*/
public class CountryCode
{
	private static final Map<String, String> countries;
	static
	{
		Map<String, String> aMap = new HashMap<String, String>(250);
		aMap.put("AF", "Afghanistan");
		aMap.put("AL", "Albania");
		aMap.put("DZ", "Algeria");
		aMap.put("AS", "American Samoa");
		aMap.put("AD", "Andorra");
		aMap.put("AO", "Angola");
		aMap.put("AI", "Anguilla");
		aMap.put("AQ", "Antarctica");
		aMap.put("AG", "Antigua and Barbuda");
		aMap.put("AR", "Argentina");
		aMap.put("AM", "Armenia");
		aMap.put("AW", "Aruba");
		aMap.put("AU", "Australia");
		aMap.put("AT", "Austria");
		aMap.put("AZ", "Azerbaijan");
		aMap.put("BH", "Bahrain");
		aMap.put("BD", "Bangladesh");
		aMap.put("BB", "Barbados");
		aMap.put("BY", "Belarus");
		aMap.put("BE", "Belgium");
		aMap.put("BZ", "Belize");
		aMap.put("BJ", "Benin");
		aMap.put("BM", "Bermuda");
		aMap.put("BT", "Bhutan");
		aMap.put("BO", "Bolivia");
		aMap.put("BA", "Bosnia and Herzegovina");
		aMap.put("BW", "Botswana");
		aMap.put("BV", "Bouvet Island");
		aMap.put("BR", "Brazil");
		aMap.put("IO", "British Indian Ocean Territory");
		aMap.put("VG", "British Virgin Islands");
		aMap.put("BN", "Brunei");
		aMap.put("BG", "Bulgaria");
		aMap.put("BF", "Burkina Faso");
		aMap.put("BI", "Burundi");
		aMap.put("CI", "Côte d\"Ivoire");
		aMap.put("KH", "Cambodia");
		aMap.put("CM", "Cameroon");
		aMap.put("CA", "Canada");
		aMap.put("CV", "Cape Verde");
		aMap.put("KY", "Cayman Islands");
		aMap.put("CF", "Central African Republic");
		aMap.put("TD", "Chad");
		aMap.put("CL", "Chile");
		aMap.put("CN", "China");
		aMap.put("CX", "Christmas Island");
		aMap.put("CC", "Cocos (Keeling) Islands");
		aMap.put("CO", "Colombia");
		aMap.put("KM", "Comoros");
		aMap.put("CG", "Congo");
		aMap.put("CK", "Cook Islands");
		aMap.put("CR", "Costa Rica");
		aMap.put("HR", "Croatia");
		aMap.put("CU", "Cuba");
		aMap.put("CY", "Cyprus");
		aMap.put("CZ", "Czech Republic");
		aMap.put("CD", "Democratic Republic of the Congo");
		aMap.put("DK", "Denmark");
		aMap.put("DJ", "Djibouti");
		aMap.put("DM", "Dominica");
		aMap.put("DO", "Dominican Republic");
		aMap.put("TP", "East Timor");
		aMap.put("EC", "Ecuador");
		aMap.put("EG", "Egypt");
		aMap.put("SV", "El Salvador");
		aMap.put("GQ", "Equatorial Guinea");
		aMap.put("ER", "Eritrea");
		aMap.put("EE", "Estonia");
		aMap.put("ET", "Ethiopia");
		aMap.put("FO", "Faeroe Islands");
		aMap.put("FK", "Falkland Islands");
		aMap.put("FJ", "Fiji");
		aMap.put("FI", "Finland");
		aMap.put("MK", "Former Yugoslav Republic of Macedonia");
		aMap.put("FR", "France");
		aMap.put("FX", "France), Metropolitan");
		aMap.put("GF", "French Guiana");
		aMap.put("PF", "French Polynesia");
		aMap.put("TF", "French Southern Territories");
		aMap.put("GA", "Gabon");
		aMap.put("GE", "Georgia");
		aMap.put("DE", "Germany");
		aMap.put("GH", "Ghana");
		aMap.put("GI", "Gibraltar");
		aMap.put("GR", "Greece");
		aMap.put("GL", "Greenland");
		aMap.put("GD", "Grenada");
		aMap.put("GP", "Guadeloupe");
		aMap.put("GU", "Guam");
		aMap.put("GT", "Guatemala");
		aMap.put("GN", "Guinea");
		aMap.put("GW", "Guinea-Bissau");
		aMap.put("GY", "Guyana");
		aMap.put("HT", "Haiti");
		aMap.put("HM", "Heard and Mc Donald Islands");
		aMap.put("HN", "Honduras");
		aMap.put("HK", "Hong Kong");
		aMap.put("HU", "Hungary");
		aMap.put("IS", "Iceland");
		aMap.put("IN", "India");
		aMap.put("ID", "Indonesia");
		aMap.put("IR", "Iran");
		aMap.put("IQ", "Iraq");
		aMap.put("IE", "Ireland");
		aMap.put("IL", "Israel");
		aMap.put("IT", "Italy");
		aMap.put("JM", "Jamaica");
		aMap.put("JP", "Japan");
		aMap.put("JO", "Jordan");
		aMap.put("KZ", "Kazakhstan");
		aMap.put("KE", "Kenya");
		aMap.put("KI", "Kiribati");
		aMap.put("KW", "Kuwait");
		aMap.put("KG", "Kyrgyzstan");
		aMap.put("LA", "Laos");
		aMap.put("LV", "Latvia");
		aMap.put("LB", "Lebanon");
		aMap.put("LS", "Lesotho");
		aMap.put("LR", "Liberia");
		aMap.put("LY", "Libya");
		aMap.put("LI", "Liechtenstein");
		aMap.put("LT", "Lithuania");
		aMap.put("LU", "Luxembourg");
		aMap.put("MO", "Macau");
		aMap.put("MG", "Madagascar");
		aMap.put("MW", "Malawi");
		aMap.put("MY", "Malaysia");
		aMap.put("MV", "Maldives");
		aMap.put("ML", "Mali");
		aMap.put("MT", "Malta");
		aMap.put("MH", "Marshall Islands");
		aMap.put("MQ", "Martinique");
		aMap.put("MR", "Mauritania");
		aMap.put("MU", "Mauritius");
		aMap.put("YT", "Mayotte");
		aMap.put("MX", "Mexico");
		aMap.put("FM", "Micronesia");
		aMap.put("MD", "Moldova");
		aMap.put("MC", "Monaco");
		aMap.put("MN", "Mongolia");
		aMap.put("ME", "Montenegro");
		aMap.put("MS", "Montserrat");
		aMap.put("MA", "Morocco");
		aMap.put("MZ", "Mozambique");
		aMap.put("MM", "Myanmar");
		aMap.put("NA", "Namibia");
		aMap.put("NR", "Nauru");
		aMap.put("NP", "Nepal");
		aMap.put("NL", "Netherlands");
		aMap.put("AN", "Netherlands Antilles");
		aMap.put("NC", "New Caledonia");
		aMap.put("NZ", "New Zealand");
		aMap.put("NI", "Nicaragua");
		aMap.put("NE", "Niger");
		aMap.put("NG", "Nigeria");
		aMap.put("NU", "Niue");
		aMap.put("NF", "Norfolk Island");
		aMap.put("KP", "North Korea");
		aMap.put("MP", "Northern Marianas");
		aMap.put("NO", "Norway");
		aMap.put("OM", "Oman");
		aMap.put("PK", "Pakistan");
		aMap.put("PW", "Palau");
		aMap.put("PS", "Palestine");
		aMap.put("PA", "Panama");
		aMap.put("PG", "Papua New Guinea");
		aMap.put("PY", "Paraguay");
		aMap.put("PE", "Peru");
		aMap.put("PH", "Philippines");
		aMap.put("PN", "Pitcairn Islands");
		aMap.put("PL", "Poland");
		aMap.put("PT", "Portugal");
		aMap.put("PR", "Puerto Rico");
		aMap.put("QA", "Qatar");
		aMap.put("RE", "Reunion");
		aMap.put("RO", "Romania");
		aMap.put("RU", "Russia");
		aMap.put("RW", "Rwanda");
		aMap.put("ST", "São Tomé and Príncipe");
		aMap.put("SH", "Saint Helena");
		aMap.put("PM", "St. Pierre and Miquelon");
		aMap.put("KN", "Saint Kitts and Nevis");
		aMap.put("LC", "Saint Lucia");
		aMap.put("VC", "Saint Vincent and the Grenadines");
		aMap.put("WS", "Samoa");
		aMap.put("SM", "San Marino");
		aMap.put("SA", "Saudi Arabia");
		aMap.put("SN", "Senegal");
		aMap.put("RS", "Serbia");
		aMap.put("SC", "Seychelles");
		aMap.put("SL", "Sierra Leone");
		aMap.put("SG", "Singapore");
		aMap.put("SK", "Slovakia");
		aMap.put("SI", "Slovenia");
		aMap.put("SB", "Solomon Islands");
		aMap.put("SO", "Somalia");
		aMap.put("ZA", "South Africa");
		aMap.put("GS", "South Georgia and the South Sandwich Islands");
		aMap.put("KR", "South Korea");
		aMap.put("ES", "Spain");
		aMap.put("LK", "Sri Lanka");
		aMap.put("SD", "Sudan");
		aMap.put("SR", "Suriname");
		aMap.put("SJ", "Svalbard and Jan Mayen Islands");
		aMap.put("SZ", "Swaziland");
		aMap.put("SE", "Sweden");
		aMap.put("CH", "Switzerland");
		aMap.put("SY", "Syria");
		aMap.put("TW", "Taiwan");
		aMap.put("TJ", "Tajikistan");
		aMap.put("TZ", "Tanzania");
		aMap.put("TH", "Thailand");
		aMap.put("BS", "The Bahamas");
		aMap.put("GM", "The Gambia");
		aMap.put("TG", "Togo");
		aMap.put("TK", "Tokelau");
		aMap.put("TO", "Tonga");
		aMap.put("TT", "Trinidad and Tobago");
		aMap.put("TN", "Tunisia");
		aMap.put("TR", "Turkey");
		aMap.put("TM", "Turkmenistan");
		aMap.put("TC", "Turks and Caicos Islands");
		aMap.put("TV", "Tuvalu");
		aMap.put("VI", "US Virgin Islands");
		aMap.put("UG", "Uganda");
		aMap.put("UA", "Ukraine");
		aMap.put("AE", "United Arab Emirates");
		aMap.put("GB", "United Kingdom");
		aMap.put("US", "United States");
		aMap.put("UM", "United States Minor Outlying Islands");
		aMap.put("UY", "Uruguay");
		aMap.put("UZ", "Uzbekistan");
		aMap.put("VU", "Vanuatu");
		aMap.put("VA", "Vatican City");
		aMap.put("VE", "Venezuela");
		aMap.put("VN", "Vietnam");
		aMap.put("WF", "Wallis and Futuna Islands");
		aMap.put("EH", "Western Sahara");
		aMap.put("YE", "Yemen");
		aMap.put("ZM", "Zambia");
		aMap.put("ZW", "Zimbabwe");  
		countries = Collections.unmodifiableMap(aMap);
	}

	public static String getCountry(String code)
	{
		if (code == null || code.trim().length() != 2)
		{
			throw new WorkbenchException("Country code must be exactly 2 characters per the ISO 3166-1 standard.");
		}
		String country = countries.get(code);
		if (country == null)
		{
			throw new WorkbenchException(code + " is not a permitted country code.");
		}
		return country; 
	}

	public static boolean isValid(String code)
	{
		try
		{
			CountryCode.getCountry(code);
			return true;
		}
		catch(WorkbenchException we)
		{
			return false;
		}
	}
}



