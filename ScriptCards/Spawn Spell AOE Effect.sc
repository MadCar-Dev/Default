!script {{

  --/|Script Name : Spawn AOE Spell Effect 
  --/|Version     : 3.0
  --/|Requires SC : 1.3.7+, SelectManager, SpawnDefaultToken*
  --/|Author      : Will M.

  --/|Description : Allows user to quickly spawn a token AOE Template
  --/|

  --/|Setup: 
  --/| 1 - A new Character Sheet named Spawn-Generic-AoE-Template
  --/| 2 - Create a rollable token table with 28 different tokens images
  --/|  4 shapes: 1-Cone, 2-Square, 3-Line, 4-Sphere/Circle)
  --/|  7 Colors: 1-Acid Green, 2-Cold Blue, 3-Lightning Blue, 4-Fire Red, 5-Light Yellow, 6-Magic Purple, 7-Dark Purple

  --/| To get a desired token side the math is: (Color-1)*4 + Shape/Tempate
  --/| So a Fire Red Sphere would be (4-1)*4 + 4 or 16
  --/| 3 - Assign this multisided Rollable Token to the Spawn-Generic-AoE-Template character sheet

--#whisper|
--#debug|1
--#title|Spawn Spell Template
--#bodyFontSize|11px
--#sourceToken|@{selected|token_id}
--#activepage|[*S:t-_pageid]

  --=SnapInc|1 --=Scale|1  --=DivFactor|1
	--=SnapInc|[*P:snapping_increment] --~SnapInc|math;max;1;[$SnapInc]
  --=Scale|[*P:scale_number] --~Scale|math;max;1;[$Scale]	
  --=DivFactor|[$SnapInc] * [$Scale] / 5

  --=template|?{Template?|Cone,1|Cube,2|Line,3|Sphere,4}
  --=color|?{Color?|Acid Green,1|Cold Blue,2|Lightning Blue,3|Fire Red,4|Light Yellow,5|Magic Purple,6|Dark Purple,7}
  --=size|?{Size (Cirle-Radius, Cube/Line/Cone-Length)|10|15|20|30|40|50|60|80|100|120}
  --=side|[$color] - 1
  --=side|[$side] * 4
  --=side|[$side] + [$template]


	--+|Template([$template]) color([$color]) side([$side])
	--+|SnapInc([$SnapInc]) Scale([$Scale]) DF([$DivFactor])

  --C[$template]|1:>CONE;[$template]|2:>CUBE;[$template]|3:>LINE;[$template]|4:>SPHERE;[$template]

  --X| Exit

--:CONE| Cone
    --=wlen|[$size]/5
    --=hlen|100 * [$wlen] / 1.3333
    --=hlen|[$hlen] / 100

    --=wlen|[$wlen] / [$DivFactor]
    --=hlen|[$hlen] / [$DivFactor]

    --#leftsub|Cone ([$size] ft.)
    --#rightsub|WxH: [$wlen]x[$hlen]

   --=horz|[$wlen] / 2 + .5
   --=vert|[$hlen] / 2 - .5

   --=horz|0
   --=vert|0
 

   --=rotate|135+22.5

        --@forselected+|Spawn _name|Spawn-Generic-AoE-Template _offset|[$horz],[$vert] _side|[$side] _expand|40,20 _size|[$wlen],[$hlen] _rotation|[$rotate] _order|ToBack
    --<|

--:CUBE| cube
    --#leftsub|Cube (Width and Height: [$size] ft.)
    --=len|[$size]/5
    --=len|[$len] / [$DivFactor]    
    --=offset|[$len]/ 2 + 1.5
    --+len and offset|[$len] : [$offset]
        --@forselected+|Spawn _name|Spawn-Generic-AoE-Template _offset|[$offset],-0.5 _side|[$side] _expand|40,20 _size|[$len],[$len] _order|ToBack

--<|

--:LINE| Line
    --#leftsub|Line (Length: [$size] ft. x 5 ft.)
    --=len|[$size]/5
    --=len|[$len] / [$DivFactor]
    --=width|1 / [$DivFactor]    
    --=offset|[$len] / 2 + .5
    --+len and offset|[$len] : [$offset]

    --@forselected+|Spawn _name|Spawn-Generic-AoE-Template _offset|[$offset],0 _side|[$side] _expand|40,20 _size|[$len],[$width] _order|ToBack

--<|

--:SPHERE| Sphere
    --#leftsub|Sphere (Radius: [$size] ft.)
    --=len|[$size] / 5 * 2

    --=len|[$len] / [$DivFactor]

    --=offset|[$len] / 2 + 0.5
    --+len and offset|[$len] : [$offset]

    --@forselected+|Spawn _name|Spawn-Generic-AoE-Template _offset|[$offset],0 _side|[$side] _expand|40,20 _size|[$len],[$len] _order|ToBack
--<|

--:LOG|debug level;hdr;msg
  --\|debug level is used to show more or less debug messages.  1 is high level, 2 is detailed ...
  --*[%1%]|[%2%]; [%3%]
  
  --?[%1%] -gt [&DBUG]|LOG_EXIT
    --*[%2%]|[%3%]
  --:LOG_EXIT|
--<|

}}