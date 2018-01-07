CREATE TABLE oil_price (
  id string,
  latitude double,
  longitude double,
  cp string,
  pop string,
  adresse string,
  ville string,
  prices array<struct<id: string, nom: string, valeur: double, maj: timestamp>>
);
