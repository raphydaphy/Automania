public static class FalloffGenerator
{
    public static float[,,] GenerateFalloffMap(int size)
    {
        var map = new float[size, size, size];

        for (var x = 0; x < size; x++)
        {
            for (var y = 0; y < size; y++)
            {
                for (var z = 0; z < size; z++)
                {
                    var normalY = y / (float) size * 2 - 1;

                    map[x, y, z] = normalY;
                }
            }
        }

        return map;
    }
}
