package net.maatvirtue.commonlib.packagemanager.pkg;

public class Contact
{
	private String name;
	private String email;

	public Contact()
	{
		//Do nothing
	}

	public Contact(String name, String email)
	{
		this.name = name;
		this.email = email;
	}

	public Contact(String contactText)
	{
		contactText = contactText.trim();

		if(!contactText.endsWith(">"))
			this.name = contactText;
		else
		{
			contactText = contactText.substring(0, contactText.length()-1);

			if(!contactText.contains("<"))
				throw new IllegalArgumentException("invalid contactText");

			String[] contactParts = contactText.split("<");

			if(contactParts.length!=2)
				throw new IllegalArgumentException("invalid contactText");

			this.name = contactParts[0].trim();
			this.email = contactParts[1].trim();

			if(name.equals(""))
				this.name = null;
		}
	}

	public String getContactText()
	{
		String contactText = "";

		if(name!=null)
			contactText += name;

		if(email!=null)
			contactText += " <"+email+">";

		return contactText;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}
}
