!scriptcard {{ 
  --/|Script Name : Dungeon Master Screen
  --/|Version     : 3.0
  --/|Requires SC : 1.3.7+, 
  --/|Author      : Will M.

  --/|Description : Quick DM Screen with links to DND Beyond and other resources
  --/|

  --:TOP|
  --#title|Dungeon Master Links
  --#titleCardBackground|#03038a
  --#titlefontface|Patrick Hand
  --/#oddRowBackground|#d8d8e6
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF
  --#buttonbackground|#FFFFFF
  --#buttontextcolor|#0905f2
  --#buttonbordercolor|#FFFFFF
  --#bodyFontSize|11px
  --#bodyFontSize|11px
  --#whisper|self
  --#debug|1
  --Ssettings|Web Links Tool


  --&tStyle|style="width:100%;text-align:left;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border: 0px dashed black;"
  --&trStyle1|style="border:0px dashed black;"
  --&trStyle2|style="border:0px dashed black;"
  --&tdStyle1|style="width:25%;text-align:left;background-color:#FFFFFF;font-size:100%"
  --&tdStyle2|style="width:33%;text-align:left;background-color:#FFFFFF;font-size:100%"
  --&tdStyle3|style="width:50%;text-align:center;background-color:#FFFFFF;font-size:100%"
  --&tdStyle4|style="width:100%;text-align:center;background-color:#FFFFFF;font-size:100%"

  -->DDB_LINK|Sources;sources 
  -->SECTION_HEADER|[&LINK]
  	--&tbl|[t [&tStyle]] 
  					[tr [&trStyle1]]
					  	-->DDB_LINK|PHB;sources/phb --&tbl|+ [td [&tdStyle1]] [&LINK] [/td]
					  	-->DDB_LINK|DMG;sources/dmg --&tbl|+ [td [&tdStyle1]] [&LINK] [/td]
					  	-->DDB_LINK|MM;sources/mm --&tbl|+ [td [&tdStyle1]] [&LINK] [/td]
					  	-->DDB_LINK|VG2M;sources/vgtm --&tbl|+ [td [&tdStyle1]] [&LINK] [/td]
  	--&tbl|+ [/tr] [tr [&trStyle1]]
					  	-->DDB_LINK|XG2E;sources/xgte --&tbl|+ [td [&tdStyle1]] [&LINK] [/td]
					  	-->DDB_LINK|TCOE;sources/tcoe --&tbl|+ [td [&tdStyle1]] [&LINK] [/td]
					  	-->DDB_LINK|SCAG;sources/scag --&tbl|+ [td [&tdStyle1]] [&LINK] [/td]
					  	-->DDB_LINK|OOTA;sources/oota --&tbl|+ [td [&tdStyle1]] [&LINK] [/td]
  	--&tbl|+ [/tr] [tr [&trStyle1]]
					  	-->DDB_LINK|VRGTR;sources/vrgtr --&tbl|+ [td [&tdStyle1]] [&LINK] [/td]
					  	-->DDB_LINK|SAC;sources/sac --&tbl|+ [td [&tdStyle1]] [&LINK] [/td]
					  	--&LINK| --&tbl|+ [td [&tdStyle1]] [&LINK] [/td]
					  	--&LINK| --&tbl|+ [td [&tdStyle1]] [&LINK] [/td]
  	--&tbl|+ [/tr] [/t]
  	--+|[&tbl]

  -->DDB_LINK|Character Setup;essentials/creating-a-character 
  -->SECTION_HEADER|[&LINK]
  	--&tbl|[t [&tStyle]] 
  					[tr [&trStyle1]]
					  	-->DDB_LINK|Races;races --&tbl|+ [td [&tdStyle1]] [&LINK] [/td]
					  	-->DDB_LINK|Classes;classes --&tbl|+ [td [&tdStyle1]] [&LINK] [/td]
					  	-->DDB_LINK|Spells;spells --&tbl|+ [td [&tdStyle1]] [&LINK] [/td]
					  	-->DDB_LINK|Feats;feats --&tbl|+ [td [&tdStyle1]] [&LINK] [/td]
					  	--&tbl|+ [/tr] [tr [&trStyle1]]
  	--&tbl|+ [t [&tStyle]] 
  						[tr [&trStyle1]]
						  	-->DDB_LINK|Backgrounds;backgrounds --&tbl|+ [td [&tdStyle3]] [&LINK] [/td]
					  		-->DDB_LINK|Equipment;equipment --&tbl|+ [td [&tdStyle3]] [&LINK] [/td]
  	--&tbl|+ [/tr] [tr [&trStyle1]]  	
						  	-->DDB_LINK|Magic Items;magic-items --&tbl|+ [td [&tdStyle3]] [&LINK] [/td]
						  	-->DDB_LINK|Monsters;monsters --&tbl|+ [td [&tdStyle3]] [&LINK] [/td]
  	--&tbl|+ [/tr] [/t]
  	--+|[&tbl]


  -->DDB_LINK|Combat;sources/phb/combat
  -->SECTION_HEADER|[&LINK]
  	--&tbl|[t [&tStyle]] 
	 					[tr [&trStyle1]]
					  	-->DDB_LINK|Actions;sources/phb/combat#ActionsinCombat --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->DDB_LINK|Readying;https:sources/phb/combat#Ready --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->DDB_LINK|Conditions;sources/phb/appendix-a-conditions#AppendixAConditions --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	--&tbl|+ [/tr] [tr [&trStyle1]]
  	--&tbl|+ [t [&tStyle]] 
  						[tr [&trStyle1]]
					  	-->DDB_LINK|Cover;sources/phb/combat#Cover --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->DDB_LINK|Grapple;sources/phb/combat#Grappling --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->DDB_LINK|Jumping;sources/basic-rules/adventuring#Jumping --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
  	--&tbl|+ [/tr] [tr [&trStyle1]]  	
					  	-->DDB_LINK|Stealth;sources/basic-rules/combat#UnseenAttackersandTargets --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->DDB_LINK|Suprise;sources/basic-rules/combat#Surprise --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->DDB_LINK|Death;sources/phb/combat#Droppingto0HitPoints --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
  	--&tbl|+ [/tr] [tr [&trStyle1]]  	
					  	-->DDB_LINK|Conentrating;https:sources/phb/spellcasting#Concentration --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->DDB_LINK|Suffocating;sources/phb/adventuring#Suffocating --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->DDB_LINK|Lingering Injuries;sources/dmg/dungeon-masters-workshop#Injuries --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
  	--&tbl|+ [/tr] [/t]
  	--+|[&tbl]

  -->DDB_LINK|Adventuring;sources/phb/Adventuring
  -->SECTION_HEADER|[&LINK]
  	--&tbl|[t [&tStyle]] 
  					[tr [&trStyle1]]
					  	-->DDB_LINK|Money;sources/phb/equipment#StandardExchangeRates --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->DDB_LINK|Armor;sources/phb/equipment#ArmorandShields --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->DDB_LINK|Weapons;sources/phb/equipment#Weapons --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	--&tbl|+ [/tr] [tr [&trStyle1]]
  	--&tbl|+ [t [&tStyle]] 
  						[tr [&trStyle1]]
					  	-->DDB_LINK|Gear;sources/phb/equipment#AdventuringGear --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->DDB_LINK|Tools;sources/phb/equipment#Tools --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->DDB_LINK|Travel;sources/phb/adventuring#Movement --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
  	--&tbl|+ [/tr] [tr [&trStyle1]]  	
					  	-->DDB_LINK|Vision/Light;sources/phb/adventuring#VisionandLight --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->DDB_LINK|Checks;sources/phb/using-ability-scores#UsingEachAbility --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->DDB_LINK|Saves;sources/phb/using-ability-scores#SavingThrows --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
  	--&tbl|+ [/tr] [tr [&trStyle1]]  	
					  	-->DDB_LINK|Spellcasting;sources/phb/spellcasting --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->DDB_LINK|Spell Schools;sources/phb/spellcasting#AttackRolls --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->DDB_LINK|Wild Magic;sources/phb/classes#WildMagic --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
  	--&tbl|+ [/tr] [tr [&trStyle1]]  	
					  	-->DDB_LINK|Madness;sources/dmg/running-the-game#Madness --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->DDB_LINK|Disease;sources/dmg/running-the-game#Diseases --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->DDB_LINK|Poison;sources/dmg/running-the-game#Poisons --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
  	--&tbl|+ [/tr] [tr [&trStyle1]]  	
					  	-->DDB_LINK|Hazards;sources/dmg/adventure-environments#DungeonHazards --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->DDB_LINK|Chases;sources/dmg/running-the-game#Chases --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->DDB_LINK|Downtime;sources/phb/adventuring#BetweenAdventures --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
  	--&tbl|+ [/tr] [/t]
  	--+|[&tbl]

  -->WEB_LINK|Miscellaneous Tools;https://donjon.bin.sh/5e/
  -->SECTION_HEADER|[&LINK]
  	--&tbl|[t [&tStyle]] 
  					[tr [&trStyle1]]
					  	-->WEB_LINK|NPC Creation;https://rpgtinker.com/ --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->WEB_LINK|Names;https://www.fantasynamegenerators.com/dungeons-and-dragons.php --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->WEB_LINK|DonJon;https://donjon.bin.sh/5e/random/ --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	--&tbl|+ [/tr] [tr [&trStyle1]]
  	--&tbl|+ [t [&tStyle]] 
  						[tr [&trStyle1]]
					  	-->WEB_LINK|Kassoon;https://www.kassoon.com/dnd/ --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->WEB_LINK|Open;sources\phb --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
					  	-->WEB_LINK|open;sources\dmg --&tbl|+ [td [&tdStyle2]] [&LINK] [/td]
  	--&tbl|+ [/tr] [/t]
  	--+|[&tbl]

--X|

--/|I use D&D Beyond for my rules refrences.  These functions create hyperlinks based on context of card
--:DDB_LINK|Name; Ref (ref assumes that https://www.dndeyond.com/ will be prefixed)
  --/|Format is https://www.dndbeyond.com/Ref
  --/| returns [&LINK] as a global link
  --&LINK|[button][%1%]::https://www.dndbeyond.com/[%2%][/button]
--<|

--:WEB_LINK|Name; Ref 
  --&LINK|[button][%1%]::[%2%][/button]
--<|

--:DNDBEYOND_MONSTER_LINK|MonsterName 
  --/|Format is https://www.dndbeyond.com/monsters/vampire-spellcaster
  --~MN|string;replace; ;-;[%1%]
  --&zDnDBeyondMonsterLink|https://www.dndbeyond.com/monsters/[&MN]
--<|

--:DNDBEYOND_SPELL_LINK|SpellName 
  --/|Format is https://www.dndbeyond.com/spells/dominate-person
  --~SN|string;replace; ;-;[%1%]
  --&zDnDBeyondSpellLink|https://www.dndbeyond.com/spells/[&SN]
--<|

--:SECTION_HEADER|Title
  --&hdrstyle_T|style="width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 1px black;border: 0px solid black;"
  --&hdrstyle_TR|style="border:0px solid black;"
  --&hdrstyle_TD|style="width:100%;background-color:#FFFFFF;font-size:110%;font-weight:bold;text-align:center"
  --+|[t [&hdrstyle_T]][tr [&hdrstyle_TR]][td [&hdrstyle_TD]][c][%1%][/c][/td][/tr][/t]
--<|

}}