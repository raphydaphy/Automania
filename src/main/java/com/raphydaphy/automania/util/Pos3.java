package main.java.com.raphydaphy.automania.util;

public class Pos3
{
	public int x;
	public int y;
	public int z;

	public Pos3(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public boolean equals(Object other)
	{
		if (this == other) return true;
		if (other == null || getClass() != other.getClass())
		{
			return false;
		}

		Pos3 pos3 = (Pos3) other;

		if (x != pos3.x || y != pos3.y)
		{
			return false;
		}

		return z == pos3.z;
	}

	@Override
	public int hashCode()
	{
		int result = x;
		result = 31 * result + y;
		result = 31 * result + z;
		return result;
	}
}
