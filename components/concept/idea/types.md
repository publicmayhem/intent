Ultimately every type is defined and tied to concepts

1. LocalDateTime is a concept of a date
2. LocalDate is a concept of a date
3. dd/MM/yyyy is a concept of a date

You can define a base type which can be re-used

date/local-datetime
* is a concept:date
* can derive date, time, year, month, day of month etc

Actual types can be derived from base type
* :foo/start-time -> date/local-datetime
* :bar/started -> :foo/start-time

Validation and generation based on spec
* date/local-datetime
  * spec :date/local-datetime
  * gen :date/local-datetime

Distinct
A concrete type can only exist once within a structure, if a copy is to be made, then it must be renamed with a unique type and unique keys

```
{
  :foo [:bar]
  :mee :soo
}
```
where :soo is basically a type :bar, but must be unique

Distinctness allows easy referencing with app, i.e. for ui, a form that updates :bar/postcode can expect :foo[31] data to be copied to :mee as type :soo and referenced directly as :mee/postcode as opposed to :foo[31]/postcode
