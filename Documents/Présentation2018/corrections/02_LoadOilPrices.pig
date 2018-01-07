DEFINE XPath org.apache.pig.piggybank.evaluation.xml.XPath();

A = LOAD 'PrixCarburants_instantane.xml' USING org.apache.pig.piggybank.storage.XMLLoader('pdv') as (pdv:chararray);
B = FOREACH A GENERATE
      XPath(pdv,'pdv/@id') as id,
      (double)XPath(pdv,'pdv/@latitude') as latitude,
      (double)XPath(pdv,'pdv/@longitude') as longitude,
      XPath(pdv,'pdv/@cp') as cp,
      XPath(pdv,'pdv/@pop') as pop,
      XPath(pdv,'pdv/adresse') as adresse,
      XPath(pdv,'pdv/ville') as ville,
      TOBAG(
        TOTUPLE(XPath(pdv,'pdv/prix[1]/@id'),XPath(pdv,'pdv/prix[1]/@nom'),(double)XPath(pdv,'pdv/prix[1]/@valeur'),XPath(pdv,'pdv/prix[1]/@maj')),
        TOTUPLE(XPath(pdv,'pdv/prix[2]/@id'),XPath(pdv,'pdv/prix[2]/@nom'),(double)XPath(pdv,'pdv/prix[2]/@valeur'),XPath(pdv,'pdv/prix[2]/@maj')),
        TOTUPLE(XPath(pdv,'pdv/prix[3]/@id'),XPath(pdv,'pdv/prix[3]/@nom'),(double)XPath(pdv,'pdv/prix[3]/@valeur'),XPath(pdv,'pdv/prix[3]/@maj')),
        TOTUPLE(XPath(pdv,'pdv/prix[4]/@id'),XPath(pdv,'pdv/prix[4]/@nom'),(double)XPath(pdv,'pdv/prix[4]/@valeur'),XPath(pdv,'pdv/prix[4]/@maj')),
        TOTUPLE(XPath(pdv,'pdv/prix[5]/@id'),XPath(pdv,'pdv/prix[5]/@nom'),(double)XPath(pdv,'pdv/prix[5]/@valeur'),XPath(pdv,'pdv/prix[5]/@maj')),
        TOTUPLE(XPath(pdv,'pdv/prix[6]/@id'),XPath(pdv,'pdv/prix[6]/@nom'),(double)XPath(pdv,'pdv/prix[6]/@valeur'),XPath(pdv,'pdv/prix[6]/@maj')),
        TOTUPLE(XPath(pdv,'pdv/prix[7]/@id'),XPath(pdv,'pdv/prix[7]/@nom'),(double)XPath(pdv,'pdv/prix[7]/@valeur'),XPath(pdv,'pdv/prix[7]/@maj')),
        TOTUPLE(XPath(pdv,'pdv/prix[8]/@id'),XPath(pdv,'pdv/prix[8]/@nom'),(double)XPath(pdv,'pdv/prix[8]/@valeur'),XPath(pdv,'pdv/prix[8]/@maj')),
        TOTUPLE(XPath(pdv,'pdv/prix[9]/@id'),XPath(pdv,'pdv/prix[9]/@nom'),(double)XPath(pdv,'pdv/prix[9]/@valeur'),XPath(pdv,'pdv/prix[9]/@maj')),
        TOTUPLE(XPath(pdv,'pdv/prix[10]/@id'),XPath(pdv,'pdv/prix[10]/@nom'),(double)XPath(pdv,'pdv/prix[10]/@valeur'),XPath(pdv,'pdv/prix[10]/@maj'))
      ) as prices:{(id: chararray, nom: chararray, valeur: double, maj: chararray)};
C = FOREACH B {
  TMP_PRICES_A = FILTER prices BY id IS NOT NULL AND id!='';
  TMP_PRICES_B = FOREACH TMP_PRICES_A GENERATE id, nom, valeur, ToDate(maj,'yyyy-MM-dd HH:mm:ss') as maj;
  GENERATE id, latitude, longitude, cp, pop, adresse, ville, TMP_PRICES_B as prices;
}
STORE C INTO 'oil_price' USING org.apache.hive.hcatalog.pig.HCatStorer();
