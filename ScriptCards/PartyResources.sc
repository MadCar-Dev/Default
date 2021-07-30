!scriptcard  {{ 
  
  --/|Script Name : Party Resources
  --/|Version     : 1.0
  --/|Requires SC : 1.3.7+, 
  --/|Author      : Will M.

  --/|Description : Reports on Class and Other resourc availability.

  --/|Dev note    : Character Names are hard-coded, need to update to be more dynamic.


  --#title|Party Resources
  --#titleCardBackground|#932729
  --#oddRowBackground|#FFFFFF
  --#evenRowBackground|#FFFFFF
  --#bodyFontSize|12px
   --#whisper|gm

 	--+|[t style="width:100%"]
			[tr][td style="width:20%"][b]Name[/b][/td][td style="width:50%"][b]Resource[/b][/td][td style="width:30%"][b]Available / Maximum [/b][/td][/tr]
  		[tr][td]Kharg[/td][td]@{Kharg|class_resource_name}[/td][td]@{Kharg|class_resource} / @{Kharg|class_resource|max}[/td][/tr]
      [tr][td][/td][td]@{Kharg|other_resource_name}[/td][td]@{Kharg|other_resource} / @{Kharg|other_resource|max}[/td][/tr]
      [tr style="background-color:#f0ede9;color:#000000;"][td]Eliza[/td][td]@{Eliza Redburrow|class_resource_name}[/td][td]@{Eliza Redburrow|class_resource} / @{Eliza Redburrow|class_resource|max}[/td][/tr]
      [tr style="background-color:#f0ede9;color:#000000;"][td][/td][td]@{Eliza Redburrow|other_resource_name}[/td][td]@{Eliza Redburrow|other_resource} / @{Eliza Redburrow|other_resource|max}[/td][/tr]
      [tr][td]Jokaryn[/td][td]@{Jokaryn|class_resource_name}[/td][td]@{Jokaryn|class_resource} / @{Jokaryn|class_resource|max}[/td][/tr]
      [tr][td][/td][td]@{Jokaryn|other_resource_name}[/td][td]@{Jokaryn|other_resource} / @{Jokaryn|other_resource|max}[/td][/tr]
      [tr style="background-color:#f0ede9;color:#000000;"][td]Xaral[/td][td]@{Xaral Mind-drinker|class_resource_name}[/td][td]@{Xaral Mind-drinker|class_resource} / @{Xaral Mind-drinker|class_resource|max}[/td][/tr]
      [tr style="background-color:#f0ede9;color:#000000;"][td][/td][td]@{Xaral Mind-drinker|other_resource_name}[/td][td]@{Xaral Mind-drinker|other_resource} / @{Xaral Mind-drinker|other_resource|max}[/td][/tr]
      [tr][td]Shaper[/td][td]@{Shaper|class_resource_name}[/td][td]@{Shaper|class_resource} / @{Shaper|class_resource|max}[/td][/tr]
      [tr][td][/td][td]@{Shaper|other_resource_name}[/td][td]@{Shaper|other_resource} / @{Shaper|other_resource|max}[/td][/tr]
		[/t]
}}