!scriptcard  {{ 
  --/|Script Name : Barbarian Rages - Path of Wild Magic
  --/|Version     : 2.0
  --/|Requires SC : 1.3.7+, TokenMod, Chatsetattr
  --/|Author      : Will M.

  --/|Description : A custom Barbarian - Path of Wild Magic script for a player

  --&CharName|Kharg

  --#title|[&CharName] Rages!!!
  --#leftsub|Path of Wild Magic Feature
  --#titleCardBackground|#932729
  --#oddRowBackground|#CEC7B6
  --#evenRowBackground|#B6AB91
  --#whisper|
  --+|[img]https://media.giphy.com/media/3ohzdODFjYLku719ny/giphy.gif[/img] 

  --&TokenId|@{selected|token_id}
  --&CharId|[*[&TokenId]:t-represents]
  --=Rages|[*[&CharId]:class_resource]

  --?[$Rages] -le 0|NO_MORE_RAGES

  --:___Primary Rolls___|
  --=DC|8 + @{selected|pb} + @{selected|constitution_mod}
  --=SurgeRoll|1d8

  --:___Conditionals___|
  --?[$SurgeRoll] -eq 1| >Roll_1
  --?[$SurgeRoll] -eq 2| >Roll_2
  --?[$SurgeRoll] -eq 3| >Roll_3
  --?[$SurgeRoll] -eq 4| >Roll_4
  --?[$SurgeRoll] -eq 5| >Roll_5
  --?[$SurgeRoll] -eq 6| >Roll_6
  --?[$SurgeRoll] -eq 7| >Roll_7
  --?[$SurgeRoll] -eq 8| >Roll_8

  --=Rages|[$Rages] - 1

  --@token-mod|_ids [&TokenId] _set statusmarkers|Rage
  --/@modattr|_charid [&CharId] _silent _evaluate _class_resource|-1
  --@setattr|_charid [&CharId] _silent _repeating_damagemod_$0_global_damage_active_flag|1 _class_resource|[$Rages.Total] _Raging|2 _rage_text| plus raging 

  --~|turnorder;addcustom;[&CharName] Rages;1

  --+|[br][b][&CharName][/b] has [b][$Rages.Total][/b] rages left


  --X|___Exit Macro___

  --:___Functions___
  --:Roll_1|
      --=NecroDam|1d12
      --=TempHP|1d12
      --+[c][#030bfc]**~~Shadow Tendrils(1)~~|**[/#][/c]
      --+|Shadowy tendrils lash around you. 
      --+|[j]Each creature of your choice that you can see within 30 feet of you must succeed on a **[#990000]Constitution saving throw (DC [$DC.Total])[/#]** or take [$NecroDam] necrotic damage.[/j]
      --+|You also gain [$TempHP] temporary hit points.
      --<|

  --:Roll_2|
      --+[c][#030bfc]**~~Teleport(2)~~|**[/#][/c]
      --+|[j]**You teleport up to 30 feet** to an unoccupied space you can see.[/j]
      --+Ongoing Effect|[j]Until your rage ends, you can use this effect again on each of your turns as a bonus action.[/j]
      --<|

  --:Roll_3|
      --=ForceDam|1d6
      --+[c][#030bfc]**~~Intangible Spirit(3)~~|**[/#][/c]
      --+|[j]An intangible spirit, which looks like a **flumph** or a **pixie** (your choice), appears within 5 feet of one creature of your choice that you can see within 30 feet of you.[/j]
      --+|[j]At the end of the current turn, the spirit explodes, and each creature within 5 feet of it must succeed on a **[#990000]Dexterity saving throw (DC [$DC.Total])[/#]** or take [$ForceDam] (1d6) force damage.[/j] 
      --+Ongoing Effect|[j]Until your rage ends, you can use this effect again, summoning another spirit, on each of your turns as a bonus action.[/j]
      --<|

  --:Roll_4|
      --+[c][#030bfc]**~~Force Weapon(4)~~|**[/#][/c]
      --+|[j]Magic infuses one weapon of your choice that you are holding.[/j]
      --+|[j]Until your rage ends, the **weapon's damage type changes to force, and it gains the light and thrown properties**, with a normal range of 20 feet and a long range of 60 feet.[/j]
      --+|[j]If the weapon leaves your hand, the weapon reappears in your hand at the end of the current turn.[/j]
      --<|

  --:Roll_5|
      --+[c][#030bfc]**~~Retributive Strike(5)~~|**[/#][/c]
      --+Ongoing Effect|[j]Whenever a creature hits you with an attack roll before your rage ends, that creature takes **[#990000]1d6 force damage[/#]**, as magic lashes out in retribution.[/j]
      --<|

  --:Roll_6|
      --+[c][#030bfc]**~~Protective Aura(6)~~|**[/#][/c]
      --+Ongoing Effect|[j]Until your rage ends, you are surrounded by multi colored, protective lights. You gain a **[#990000]+1 bonus to AC[/#]**, and while within 10 feet of you, your allies gain the same bonus.[/j]
      --<|

  --:Roll_7|
      --+[c][#030bfc]**~~Entangling Aura(7)~~|**[/#][/c]
      --+|[j]Flowers and vines temporarily grow around you.[/j]
      --+Ongoing Effect|[j]Until your rage ends, the ground within 15 feet of you is **[#990000]difficult terrain[/#]** for your enemies.[/j]
      --<|

  --:Roll_8|
      --=RadiantDam|1d6
      --+[c][#030bfc]**~~Radiant Bolt(8)~~|**[/#][/c]
      --+|[j]A bolt of light shoots from your chest.[/j]
      --+|[j]Another creature of your choice that you can see within 30 feet of you must succeed on a **[#990000]Constitution saving throw (DC [$DC.Total])[/#]** or take [$RadiantDam] radiant damage and be blinded until the start of your next turn.[/j]
      --+Ongoing Effect|[j]Until your rage ends, you can use this effect again on each of your turns as a bonus action.[/j]
      --<|

  --:NO_MORE_RAGES|
     --+|I'm sorry, [b][&CharName][/b] has no more rages and must take a long rest to recharge
     --X|
}}