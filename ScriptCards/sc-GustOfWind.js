!script {{
  --/|Script Name : Gust of Wind Spell
  --/|Version     : 1.0
  --/|Requires SC : 1.3.9+
  --/|Author      : Will M.

  --/|Description : Implements the 5e Gust of Wind spell 
  --/|              
  

  --#title|Spell: Gust of Wind
  --#reentrant|GustOfWind
  --#debug|1
  --#sourcetoken|@{selected|token_id}
  --#targettoken|@{target|token_id}

  --&TTokenId|@{target|token_id}
  --=SpellSaveDC|[*S:spell_save_dc]
  --=Result|1d20 + [*T:strength_save_bonus]

  --?[$Result.Base] -eq 1|TARGET_FAILED
  --?[$Result.Base] -eq 20|TARGET_PASSED
  --?[$Result.Raw] -lt [$SpellSaveDC]|TARGET_FAILED
  
  --:TARGET_PASSED|
    --+|Target [b]succeeded[/b] on their strength saving throw with a [$Result] vs. Spellsave DC of [$SpellSaveDC]
    --x|

  --:TARGET_FAILED|
    --+|Target [b]failed[/b] on their strength saving throw with a [$Result] vs. Spellsave DC of [$SpellSaveDC]

    --&tStyle|style = "width:100%;padding:1px;border-spacing:0px;border-collapse:collapse;text-shadow: 0px 0px 0px black;border:0px dashed black;"
    --&trStyle1|style="border-top:0px dashed black;"
    --&tdStyle1|style="width:33%;text-align:center;background-color:#FFFFFF;font-size:80%"
    
    --/| Angle function returns a string variable (&) with lots of decimal points.
    --~A|math;angle;@{selected|token_id};@{target|token_id}

    --/| I've found that TokenMod chokes on a movement angle with lots of decimal points, so rounding is needed
    --/|$Ar will now contain a rounded integer version of &A
    --~Ar|math;round;[&A]

    --/|&Angle will end up being one of the common directions which may work better for grids
    --?[$Ar] -gt 337.5 -or [$Ar] -le 22.5|&Angle;0
    --?[$Ar] -gt 22.5 -and [$Ar] -le 67.5|&Angle;45
    --?[$Ar] -gt 67.5 -and [$Ar] -le 112.5|&Angle;90
    --?[$Ar] -gt 112.5 -and [$Ar] -le 157.5|&Angle;135
    --?[$Ar] -gt 157.5 -and [$Ar] -le 202.5|&Angle;180
    --?[$Ar] -gt 202.5 -and [$Ar] -le 247.5|&Angle;225
    --?[$Ar] -gt 247.5 -and [$Ar] -le 292.5|&Angle;270
    --?[$Ar] -gt 292.5 -and [$Ar] -le 337.5|&Angle;315

    --/| Added for dealing with a SC 1.39a bug (returns negative angles beteen 270 and 360)
    --?[$Ar] -gt -90 -and [$Ar] -le -67.5|&Angle;270
    --?[$Ar] -gt -67.5 -and [$Ar] -le -22.5|&Angle;315
    --?[$Ar] -gt -22.5 -and [$Ar] -le 0|&Angle;0

    --&UnitsFactor|1
    --?[&Angle] -eq 45|&UnitsFactor;1.4142
    --?[&Angle] -eq 135|&UnitsFactor;1.4142
    --?[&Angle] -eq 225|&UnitsFactor;1.4142
    --?[&Angle] -eq 315|&UnitsFactor;1.4142

  --=U|3 * [&UnitsFactor] 


    --/|*Angles|[&Angle] ([&A]) ([$Ar]) 
    --/| Time to call the magic TokenMod function to make the target token move across the screen
    --@token-mod|_move =[&Angle]|[$U.Raw]g _ids @{target|token_id} _ignore-selected

  --X|
}}