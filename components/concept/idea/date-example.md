Build a concept date.
1. platform agnostic concept of a date
   1. foo.date -> java.util.Date
   2. bar.date -> java.time.LocalDateTime
   3. moo.date -> String format "dd/MM/yyyy'T'hh:mm:ss"
2. They are all dates just separated by type
3. Date characteristics can be inferred 'derived'
   1. date part dd/MM/yyyy
   2. day of month
   3. month of year
   4. year
   5. possibly number of days from/to Jan 1 1970
4. Conversion fns define creation of concepts from other concepts
   1. fn:instant->date creates date from instant
   2. fn:local-datetime->date creates date from local-datetime + zoneId
   3. fn:date->instant creates instant from date
   4. fn:instant->str creates string moo.date format from instant
5. Conversion is determinant
   1. bar.date -> moo.date: given bar.date + zoneId then should be able to determine conversion sequence
      1. bar.date + zoneId -> instant -> moo.date
      2. all via fn:xxxx defined