package net.maatvirtue.commonlib.domain.packagemanager.pck;

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

	@Override
	public String toString()
	{
		return getContactText();
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Contact contact = (Contact) o;

		if(name != null ? !name.equals(contact.name) : contact.name != null) return false;
		return email != null ? email.equals(contact.email) : contact.email == null;

	}

	@Override
	public int hashCode()
	{
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (email != null ? email.hashCode() : 0);
		return result;
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
