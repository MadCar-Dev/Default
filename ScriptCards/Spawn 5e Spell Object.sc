!script {{

  --/|Script Name : Spawn 5e Spell Token 
  --/|Version     : 3.0
  --/|Requires SC : 1.3.7+, SelectManager, SpawnDefaultToken*
  --/|Author      : Will M.

  --/|Description : Allows user to quickly spawn a token based on a spell name
  --/|

  --/|Setup: 
  --/| 1 - A new Character Sheet named SpellObject
  --/| 2 - Create a rollable token table with 23 different tokens images
  --/|  (See code below for list of spells and associated side)
  --/| 3 - Assign this multisided Rollable Token to the SpellObject character sheet


--#whisper|gm,self
--#title|Spawn Spell Token
--#bodyFontSize|11px
--#sourceToken|@{selected|token_id}
--#activepage|[*S:t-_pageid]

  --=SnapInc|1 --=Scale|1  --=DivFactor|1
	--=SnapInc|[*P:snapping_increment] --~SnapInc|math;max;1;[$SnapInc]
  --=Scale|[*P:scale_number] --~Scale|math;max;1;[$Scale]	
  --=DivFactor|[$SnapInc] * [$Scale] / 5


  --=spell|?{Spell?|Burning Hands,1|Call Lightning,2|Darkness,3|Dragon's Breath,4|Faerie Fire,5|Fear,6|Fireball,7|Flame Sphere,8|Grasping Vine,9|Healing Spirit,10|Hunger of Hadar,11|Lightning Bolt,12|Mage Hand,13|Shatter,14|Silence,15|Sleep,16|Slow,17|Spiritual Weapon Axe,20|Spiritual Weapon Hammer,21|Spiritual Weapon Mace,22|Spiritual Weapon Sword,23|Summon Demon Circle,18|Web,19}

  --C[$spell]|1:>BH|2:>CL|3:>DRK|4:>DBF|5:>FF|6:>FR|7:>FB|8:>FS|9:>GV|10:>HS|11:>HOH|12:>LB|13:>MH|14:>SHTR|15:>SIL|16:>SLP|17:>SLW|18:>SDC|19:>WEB|20:>SWA|21:>SWH|22:>SWM|23:>SWS|


  --X| Exit

  --:BH| 15 foot cone
		--#leftsub|Burning Hands (15' Cone)
		--=size|4 / [$DivFactor]
    --@forselected+|Spawn _name|Spell-Object _offset|2,0 _side|[$spell] _expand|40,20 _size|[$size],[$size] _rotation|270 _order|ToBack
  --<|

  --:CL| 120 foot radius sphere (120' Radius Sphere)
		--#leftsub|Call Lightning
		--=size|24 / [$DivFactor]
    --@forselected+|Spawn _name|Spell-Object _offset|2,0 _side|[$spell] _expand|40,20 _size|[$size],[$size] _order|ToBack

  --<|

  --:DRK| Darkness
		--#leftsub|Darkness (15' Sphere)
		--=size|8 / [$DivFactor]
    --@forselected+|Spawn _name|Spell-Object _offset|3.5,-0.5 _side|[$spell] _expand|40,20 _size|[$size],[$size] _order|ToBack

  --<|

  --:DBF| Dragon's Breath - Fire
		--#leftsub|Dragon's Breath-Fire (60' Cone)
		--=size|16 / [$DivFactor]		
    --@forselected+|Spawn _name|Spell-Object _offset|6,0 _side|[$spell] _expand|20,20 _size|[$size],[$size] _rotation|270 _order|ToBack
  --<|

  --:FF| Fairie Fire
		--#leftsub|Fairie Fire (20' Cube)
		--=size|4 / [$DivFactor]
    --@forselected+|Spawn _name|Spell-Object _offset|3,-1.5 _side|[$spell] _expand|40,20 _size|[$size],[$size] _order|ToBack
  --<|

  --:FR| Fear 
		--#leftsub|Fear (30' Cone)
		--=size|7 / [$DivFactor]
    --@forselected+|Spawn _name|Spell-Object _offset|3.5,0 _side|[$spell] _expand|40,20 _size|[$size],[$size] _rotation|270 _order|ToBack
  --<|

  --:FB| Fireball
		--#leftsub|Fireball (20' Sphere)
		--=size|9.5 / [$DivFactor]
    --@forselected+|Spawn _name|Spell-Object _offset|5.5,-0.5 _side|[$spell] _expand|40,20 _size|[$size],[$size] _order|ToBack
  --<| 

  --:HOH| Hunger of Hadar 
		--#leftsub|Hunger of Hadar (20' Sphere)
		--=size|10 / [$DivFactor]
    --@forselected+|Spawn _name|Spell-Object _offset|5.5,-0.5 _side|[$spell] _expand|40,20 _size|[$size],[$size] _order|ToBack

  --<|

  --:LB| Lightning Bolt 
		--#leftsub|Lightning Bolt (100' Line)
		--=wsize|1 / [$DivFactor]
		--=hsize|21 / [$DivFactor]
    --@forselected+|Spawn _name|Spell-Object _offset|10.5,0 _side|[$spell] _expand|40,20 _size|[$wsize],[$hsize] _rotation|90 _order|ToBack

  --<|

  --:SHTR| Shatter
		--#leftsub|Shatter (10' Sphere)
		--=size|6 / [$DivFactor]
    --@forselected+|Spawn _name|Spell-Object _offset|3,-0.5 _side|[$spell] _expand|40,20 _size|[$size],[$size] _order|ToBack

  --<|

  --:SIL| Silence
		--#leftsub|Silence (20' Sphere)
		--=size|8 / [$DivFactor]
    --@forselected+|Spawn _name|Spell-Object _offset|5.5,-0.5 _side|[$spell] _expand|40,20 _size|[$size],[$size] _order|ToBack

  --<|

  --:SLP| Sleep
		--#leftsub|Sleep (20' Sphere)
		--=size|10 / [$DivFactor]
    --@forselected+|Spawn _name|Spell-Object _offset|5.5,-0.5 _side|[$spell] _expand|40,20 _size|[$size],[$size] _order|ToBack

  --<|

  --:SLW| Slow
		--#leftsub|Slow (40' Sphere)
		--=size|10 / [$DivFactor]
    --@forselected+|Spawn _name|Spell-Object _offset|6,-0.5 _side|[$spell] _expand|80,40 _size|[$size],[$size] _order|ToBack

  --<|

  --:WEB|
		--#leftsub|Web (20' Cube)
		--=size|5 / [$DivFactor]
    --@forselected+|Spawn _name|Spell-Object _offset|3.5,-.5 _side|[$spell] _expand|40,20 _size|[$size],[$size] _order|ToBack

  --<|

--:FS|
	--#leftsub|Flame Sphere
	--=size|1 / [$DivFactor]
  --@forselected+|Spawn _name|Spell-Object _offset|1,0 _side|[$spell] _expand|40,20 _size|[$size],[$size]
--<|
--:GV|
	--#leftsub|Grasping Vine
	--=size|1 / [$DivFactor]
  --@forselected+|Spawn _name|Spell-Object _offset|1,0 _side|[$spell] _expand|40,20 _size|[$size],[$size] _ rotation|180
--<|
--:HS|
	--#leftsub|Healing Spirit
	--=size|1 / [$DivFactor]
  --@forselected+|Spawn _name|Spell-Object _offset|1,0 _side|[$spell] _expand|40,20 _size|[$size],[$size] _rotation|270
--<|
--:MH|
	--#leftsub|Mage Hand
	--=size|1 / [$DivFactor]
  --@forselected+|Spawn _name|Spell-Object _offset|1,0 _side|[$spell] _expand|40,20 _size|[$size],[$size]
--<|
--:SWA|
	--#leftsub|Spiritual Weapon - Axe
	--=size|1 / [$DivFactor]
  --@forselected+|Spawn _name|Spell-Object _offset|1,0 _side|[$spell] _expand|40,20 _size|[$size],[$size]
--<|
--:SWH|
	--#leftsub|Spiritual Weapon - Hammer
	--=size|1 / [$DivFactor]
  --@forselected+|Spawn _name|Spell-Object _offset|1,0 _side|[$spell] _expand|40,20 _size|[$size],[$size]
--<|
--:SWM|
	--#leftsub|Spiritual Weapon - Mace
	--=size|1 / [$DivFactor]
  --@forselected+|Spawn _name|Spell-Object _offset|1,0 _side|[$spell] _expand|40,20 _size|[$size],[$size]
--<|
--:SWS|
	--#leftsub|Spiritual Weapon Hammer - Sword
	--=size|1 / [$DivFactor]
  --@forselected+|Spawn _name|Spell-Object _offset|1,0 _side|[$spell] _expand|40,20 _size|[$size],[$size]
--<|
--:SDC|
	--#leftsub|Summon Demon Spirit
	--=size|3 / [$DivFactor]
  --@forselected+|Spawn _name|Spell-Object _offset|2,0 _side|[$spell] _expand|40,20 _size|[$size],[$size]
--<|

}}