!scriptcard  {{ 
  --/|Script Name : Barbarian Calms Down
  --/|Version     : 2.0
  --/|Requires SC : 1.3.7+, TokenMod, Chatsetattr
  --/|Author      : Will M.

  --/|Description : Takes a barbarian out of rage, adjusting token and character sheet params

  --#title|Barbarian Calms Down
  --#titleCardBackground|#932729
  --#oddRowBackground|#CEC7B6
  --#evenRowBackground|#B6AB91
  --#whisper|gm,self
  --&TokenId|@{selected|token_id}
  --&CharId|[*[&TokenId]:t-represents]
  --=Rages|[*[&CharId]:class_resource]
  --#leftsub|Remaining Rages: [$Rages.Total]
  --@token-mod|_ids [&TokenId] _set statusmarkers|-Rage
  --@setattr|_charid [&CharId] _silent _repeating_damagemod_$0_global_damage_active_flag|0 _Raging|0 _rage_text|
  --+[*[&CharId]:character_name]| calms down and now has [b][$Rages.Total][/b] rages left.

}}