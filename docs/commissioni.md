# Gestione provvigioni e organizzazione agenti

Questo documento sintetizza i requisiti condivisi dal cliente per la gestione degli agenti attivi sul territorio lombardo e delle provvigioni associate alle fatture di vendita.

## Struttura organizzativa
- Per ogni provincia della Lombardia è presente un responsabile.
- Ogni responsabile gestisce **2 o 3 team**.
- Un team è composto da un massimo di **6 persone**: almeno **3 agenti senior**, gli altri possono essere agenti junior o stagisti.
- La produzione degli articoli non è nel perimetro del progetto (già coperta dai processi standard di Business Central).

## Regole sulle provvigioni
- La provvigione è il compenso spettante all'agente per ogni contratto concluso grazie alla sua attività.
- La percentuale viene definita contrattualmente e, nel nostro caso, varia indicativamente tra **10% e 12%**.
- Il pagamento può avvenire al saldo del cliente oppure alla firma del contratto; anche in caso di mancato pagamento del cliente l'agente riceve comunque le provvigioni.

### Provvigione di team
- Ogni team ha una **provvigione complessiva** (es. 10%).
- Ogni agente possiede una percentuale contrattuale che contribuisce a questa provvigione.
- Le percentuali interne al team sono determinate dal responsabile e riportate nei contratti.

#### Modalità di ripartizione
1. **Ripartizione percentuale**: ogni agente riceve la propria percentuale calcolata sulla provvigione del team (es. agente4 con 2% riceve 2% del 10% dell'imponibile).
2. **A sbarramento**: seguendo il ranking, gli agenti vengono remunerati fino al raggiungimento della provvigione del team (es. agenti1-3 ottengono 3% ciascuno, agente4 ottiene 1%, agente5 e agente6 non percepiscono provvigioni se la somma eccede la provvigione del team).

### Gestione delle fatture
- Le fatture di vendita sono collegate a un cliente e a un team.
- Al pagamento del cliente, la fattura viene registrata e spostata nell'elenco delle **fatture di vendita registrate**; qui le modifiche sono bloccate per garantire l'inalterabilità dei dati dopo l'incasso.
- Le provvigioni possono essere calcolate sull'importo **netto** o **lordo**, in base alle percentuali individuate nei contratti di team e di agente.

### Esempio di distribuzione
- Provvigione team: **10%**
- Agente1: **3%**
- Agente2: **3%**
- Agente3: **3%**
- Agente4: **2%**
- Agente5: **2%**
- Agente6: **1.5%**

Con la ripartizione percentuale, ciascuna quota è applicata sul 10% dell'imponibile. Con la modalità a sbarramento, le quote vengono riconosciute in ordine di ranking fino a esaurimento del 10%.
