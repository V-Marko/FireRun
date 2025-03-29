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
            { // level 8
                            {1100, 700, 100, 100,5, // x, y, width, height, speed               block
                            1000, 700, 100, 100,    // position1X, position1Y, width1, height1   pos1
                            1700, 700, 100, 100},    // position2X, position2Y, width2, height2   pos2


                {3600, 700, 100, 100, 5, // x, y, width, height, speed,
                3500, 700, 100, 100, // position1X, position1Y, width1, height1,
                4000, 700, 100, 100}, // position2X, position2Y, width2, height2

                {4500, 700, 100, 100, 5,//x, y, width, height, speed,
                4000, 700, 100, 100, // position1X, position1Y, width1, height1,
                4500, 700, 100, 100}, // position2X, position2Y, width2, height2

            },
            {//level 9
                    //x,    y,   w,   h,  s,  p1X, p1Y,  w1,  h1, p2X,  p2Y,  w2,  h2

                    {1100, 700, 100, 100, 1, 1500, 700, 100, 100, 2300, 700, 100, 100},
                    {2300, 600, 100, 100, 1, 2300, 600, 100, 100, 3000, 600, 100, 100},
            },
            {//level 10
                    {1500, 700, 100, 100, 3, 1500, 700, 100, 100, 2300, 700, 100, 100},
                    {6800, 800, 100, 100, 3, 6700, 800, 100, 100, 7500, 800, 100, 100},
            },
            {
                    //x,    y,   w,   h,  s,  p1X, p1Y,  w1,  h1, p2X,  p2Y,  w2,  h2
                    {3500, 700, 100, 100, 3, 3000, 700, 100, 100, 3700, 700, 100, 100},

            }

    };
}