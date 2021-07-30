!scriptcard  {{ 

  --/|Script Name : Healing Potions
  --/|Version     : 4.0
  --/|Requires SC : 1.3.7+, Chatsetattr
  --/|Author      : Will M.

  --/|Description : Simple Healing Potion script
  --/|              
  --/|              


  --#title|Healing Potion
  --#leftsub|Can be used as a bonus action
  --#titleCardBackground|#932729
  --#oddRowBackground|#CEC7B6
  --#evenRowBackground|#B6AB91

  --=PotionType|?{Healing Potion Type?|Healing,1|Greater Healing,2|Superior Healing,3|Supreme Healing,4}

  --:___Conditionals___|
  --?[$PotionType] -eq 1| >Healing
  --?[$PotionType] -eq 2| >Greater
  --?[$PotionType] -eq 3| >Superior
  --?[$PotionType] -eq 4| >Supreme

  --/|___Apply Healing to Selected Token|
  --/|modbattr|_charid @{selected|character_id} _HP|[$Heal]
  --@modbattr|_sel _HP|[$Heal]

  --X|___Exit Macro___

  --:___Functions___|
  --:Healing|
      --=Heal|2d4 + 2
      --+[c]**~~Healing Potion~~|**[/c]
      --+|You down a [b]Potion of Healing[/b] and are healed for [$Heal] hit points. 
      --<|

  --:Greater| 
      --=Heal|4d4 + 4 
      --+[c]**~~Greater Healing Potion~~|**[/c] 
      --+|You down a [b]Greater Potion of Healing[/b] and are healed for [$Heal] hit points.  
      --<|

  --:Superior|  
      --=Heal|8d4 + 8  
      --+[c]**~~Superior Healing Potion~~|**[/c]  
      --+|You down a [b]Superior Potion of Healing[/b] and are healed for [$Heal] hit points.   
      --<|

  --:Supreme|  
      --=Heal|10d4 + 20
      --+[c]**~~Supreme Healing Potion~~|**[/c]  
      --+|You down a [b]Supreme Potion of Healing[/b] and are healed for [$Heal] hit points.   
      --<|
}}