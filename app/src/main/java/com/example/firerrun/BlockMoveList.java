package com.example.firerrun;

public class BlockMoveList {
    public static int[][][] BlockMove = {
            {}, // level 1
            {}, // level 2
            {}, // level 3
            {}, // level 4
            {}, // level 5
            {}, // level 6
            {}, // level 7
            {
                            {1100, 700, 100, 100, 5, // x, y, width, height, speed               block
                            1000, 700, 100, 100,    // position1X, position1Y, width1, height1   pos1
                            1700, 700, 100, 100},    // position2X, position2Y, width2, height2   pos2


                {3600, 700, 100, 100, 5, // x, y, width, height, speed,
                3500, 700, 100, 100, // position1X, position1Y, width1, height1,
                4000, 700, 100, 100}, // position2X, position2Y, width2, height2

                {4500, 700, 100, 100, 5,//x, y, width, height, speed,
                4000, 700, 100, 100, // position1X, position1Y, width1, height1,
                4500, 700, 100, 100}, // position2X, position2Y, width2, height2

            }
    };
}