#Red's Screenshot Tool
This screenshot tool takes a batch of exported screenshots and makes them into nice single images. There are some options for exporting your screenshots, too.

###How to use this tool:
1. Launch the program. You need Java installed on your computer.
2. Chose if you want to export the right eye. This option will create 2 files for every screenshot! Use with care!
2. Chose if you want to use template.cfg and template.png images in a template folder.
2. Select the INPUT folder. This is the folder with the broken up images. See below for name format.
2. Select the OUTPUT folder. This is the folder where all of the complete images will be written. 
2. Select the TEMPLATE folder if you chose to use one earlier. 
2. Wait for the images to process
2. Profit!

###How to use template.cfg and template.png:
1. Create template.png. It must be big enough to contain both the top and bottom screenshot. Put it in any empty folder.
2. Create template.cfg. Put it in the template.png folder. It'll look like this:
```text
top:91,50
bottom:131,360
```

- The top: and bottom: denote where the top and bottom screen images should be placed in the template.png The first number is the X location from the top left and the second number is the Y location from the top left (always positive, includes 0).
- Notice how there is no spaces in this document. Do not add spaces. Java will yell at you.


### Name format:

Names:

* "scr_Xn_BOTTOM.xxx" (ninjahax)
* "scr_Xn_TOP_LEFT.xxx" (ninjahax)
* "scr_Xn_TOP_RIGHT.xxx" (ninjahax)
* "bot_XXXX.xxx" (NTR)
* "top_XXXX.xxx" (NTR)
* "YYYYMMDD_HHMM_UL_XXXXX.xxx (TestMenu)"
* "YYYYMMDD_HHMM_UR_XXXXX.xxx (TestMenu)"
* "YYYYMMDD_HHMM_LO_XXXXX.xxx (TestMenu)"

Where the values are:

* Xn is any amount of numbers
* XXXX is a four digit number
* XXXXX is a five digit number
* YYYYMMDD_HHMM is Year, Month, Day, Hour, and Minute.
* xxx is a picture format (usually png or bmp)

The numbers for the top screen should match up with the same number for the bottom screen.
This is how the program tells the difference between a top and bottom image pair in order to
merge them together. NTR, ninjahax, and TestMenu images can exist in the same folder and will be merged
independently! 

###Examples output with template
![An example screenshot exported with a template.](./output/ninjahax_68.png "Screenshot")
![An example screenshot exported with a template.](./output/ninjahax_119.png "Screenshot")
![An example screenshot exported with a template.](./output/ninjahax_28.png "Screenshot")
![Using the NTR file name format.](./output/ntr_0001.png "Screenshot")

###Examples output without template
![An example screenshot exported with this tool alone using the testmenu filename format.](./output/testmenu_20161206_0800_00000.png "Screenshot")
