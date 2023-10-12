// FindDoubleDashIds.js
// Roll20 API script to look for any Token or Character object with a -- in their id
on('ready', function() {
  'use strict';

  log('Find Double Dashes script loaded.');

  // Register the !findDoubleDashIds command
  on('chat:message', function(msg) {
    if (msg.type === 'api' && msg.content.indexOf('!find-dds') === 0) {

      let args = msg.content.split(/\s--/);
      let commands = [];
      if (args.length > 1) {
        commands = args[1].match(/(?:[^\s"']+|"[^"]*")+/g);
        commands = commands.map(item => item.replace(/^"|"+$/g, ''));
        log (`commands: ${commands[0]} ${commands[1]}`)
        if (commands[0] == 'ping'){
          pingToken(commands[1], 0);
        }
      } else {
        findDoubleDashIds();
      }
    }
  });
});

function pingToken(tokenId) {
  const gmId = getGMPlayerID();
  const token = getObj('graphic', tokenId);

  if (token) {
    const left = token.get('left');
    const top = token.get('top');
    const pageId = token.get('_pageid');
    sendPing(left, top, pageId, gmId, true, gmId);
  }
  function getGMPlayerID() {
    const gmPlayers = findObjs({ _type: 'player' }).filter((player) => {
      return playerIsGM(player.get('_id'));
    });
    if (gmPlayers.length > 0) {
      return gmPlayers[0].get('_id');
    } else {
      log('No GM found');
      return null;
    }
  }
}  

function findDoubleDashIds() {
  function pingToken(tokenId) {
    const gmId = getGMPlayerID();
    const token = getObj('graphic', tokenId);
  
    if (token) {
      const left = token.get('left');
      const top = token.get('top');
      const pageId = token.get('_pageid');
      sendPing(left, top, pageId, gmId, true, gmId);
    }
    function getGMPlayerID() {
      const gmPlayers = findObjs({ _type: 'player' }).filter((player) => {
        return playerIsGM(player.get('_id'));
      });
      if (gmPlayers.length > 0) {
        return gmPlayers[0].get('_id');
      } else {
        log('No GM found');
        return null;
      }
    }
  }  

  const allTokens = findObjs({ _type: 'graphic' });
  const allCharacters = findObjs({ _type: 'character' });
  const doubleDashTokens = allTokens.filter(token => token.id.includes('--'));
  const doubleDashCharacters = allCharacters.filter(char => char.id.includes('--'));

  if (doubleDashTokens.length === 0 && doubleDashCharacters.length === 0) {
    sendChat('FindDoubleDashIds', 'No tokens or characters with a -- in their ID found.');
  } else {
    let message = '';
    if (doubleDashTokens.length > 0) {
      message += '<b><i>Tokens with -- in their ID:</b></i><br>';
      doubleDashTokens.forEach(token => {
        let pageId = token.get('pageid');
        let page = getObj('page', pageId);
        if (token.get('name') =='' || token.get('name').length == 0) {
          tName = 'BLANK'
        } else {
          tName = token.get('name');
        }
        let link = `!find-dds --ping ${token.get('_id')}`
        let buttonStyle = `background-color: #00000000; color: #521E10 !important; font-weight:bold; border-radius: 0px; padding: 0px; margin: 1px 1px 1px 0px; display: inline-block`;      
        let btn = `<a style = '${buttonStyle}; !important' href='${link}'>${token.get('_id')}</a>`;
        message += `<b>${tName}</b><br>&nbsp;&nbsp;- Page:${page.get('name')}<br>&nbsp;&nbsp;- ID: ${btn})<br>`;
      });
    } else {
      message += '<b><i>No tokens with -- in their ID found.</b></i><br>';
    }
    if (doubleDashCharacters.length > 0) {
      message += '<b><i>Characters with -- in their ID:</b></i><br>';
      doubleDashCharacters.forEach(char => {
        message += `${char.get('name')}<br>&nbsp;&nbsp;- ID: ${char.id})<br>`;
      });
    } else {
      message += '<b><i>No characters with -- in their ID found.</b></i><br>';
    }
    sendChat('Find Double Dashes', '<br>' + message);
  }
}



