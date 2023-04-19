const drawCard = (deckName, cardName, playerID) => {
  const deck = findObjs({ type: "deck", name: deckName })[0];

  log('DrawCard Params deck, card, pid' + deckName + ' / ' + cardName + ' / ' + playerID);

  if (!deck) {
    sendChat("System", `Deck with name "${deckName}" not found.`);
    return;
  }
  const cards = findObjs({ type: "card", deckid: deck.id });
  const card = cards.find((c) => c.get("name") === cardName);

  if (!card) {
    sendChat("System", `Card with name "${cardName}" not found in the deck "${deckName}".`);
    return;
  }
  let cardID = card.get('id');
  // log('IDs (D,C,P)' + deck.get('_id') + ' / ' + cardID + ' / ' + playerID)
  giveCardToPlayer(card.get('_id'), playerID);

  return;
}

on("chat:message", (message) => {
  if (message.type === "api" && message.content.startsWith("!drawCard")) {
    const args = message.content.split(" ").slice(1);
      if (args.length === 3) {
        drawCard(args[0], args[1], args[2]);
      } else {
        sendChat("System", "Usage: !drawCard [Deck Name] [Card Name] [PlayerID]");
      }
    }
  });

on('ready', () => {
  const version = '0.0.1';
  log('TakeCard' + version + ' is ready!');
});

const takeCard = (playerID) => {
  takeCardFromPlayer(playerID);

  return;
}

on("chat:message", (message) => {
  if (message.type === "api" && message.content.startsWith("!takecard")) {
    const args = message.content.split(" ").slice(1);
      if (args.length === 1) {
        takeCardFromPlayer(args[0]);
      } else {
        sendChat("System", "Usage: !drawCard [Deck Name] [Card Name] [PlayerID]");
      }
    }
  });

on('ready', () => {
  const version = '0.0.1';
  log('DrawCard' + version + ' is ready!');
});