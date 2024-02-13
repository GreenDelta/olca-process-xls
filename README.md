# openLCA Excel format for process data sets

This repository contains the openLCA import and export logic for process data
sets stored in Excel files. It can be also used as a stand-alone module (see
[this example](src/test/java/org/openlca/io/xls/process/Example.java)).

## Principles

### Data linking

Each process data set is stored in a separate file, an Excel workbook. Within a
workbook, things are identified by name. Reference data are listed in separate
sheets of the workbook where then an ID (`UUID`) is assigned to the respective
name. This ID is then used to link these data sets in an import. For example,
a process could link to the location `Aruba` in the `General information` sheet.
Then, in the `Locations` sheet of the workbook there would be a row for this
location that also contains the ID. When the data set is imported, the import
first checks if there is a location with this `ID` in the database and links
it when this is the case. Otherwise, it will create that location with the
information available in the `Locations` sheet.

For flows, it is the same principle, but they are identified by name _and_
category; except for product and waste flows in the `Allocation` tab where
these flows are referenced by name. Also, locations and currencies can be
linked by their respective codes alternatively.

This principle - internal by name and external by ID - makes the workbook
readable and the import reliable, but it requires that two things cannot have
the same name (except for flows) within a workbook (which is in general a
useful rule but maybe does not work in every use case).

### Format

In a workbook the data is stored in several sheets where each sheet has a
name that identifies the content of the sheet. There are two types of sheets:
sheets that contain sections and sheets that contain a single table. A table
sheet contains multiple entities of the same type. The first row of such a
table contains the field names of these entities. The order of the columns
is not important but the field names are. For example, the exchanges of a
process are stored in the table sheets `Inputs` and `Outputs`, also the
reference data sheets `Flows`, `Units`, `Locations` etc. are table sheets.
A table sheet should not contain empty rows between filled rows.

In a sheet with sections, a section always starts with a section header
in the first column that is the identifier of the section in the sheet.
It can be followed by a set of field-value pairs or a table just like
in a table sheet. In case of field-value pairs, the field identifiers
are located in the first and the values in the second column of the sheet.
The section ends with the first empty row after the section header. For
example, the `Time` section in the `General information` sheet is a field-value
section with the fields `Valid from`, `Valid until`, and `Description`. The
section `Input parameters` in the sheet `Parameters` is a table section with
the fields `Name`, `Value`, `Uncertainty`, etc.

Below the possible sheets, sections, and fields are listed:

* Sheet `General information`:
  * Section `General information`
    * Field `UUID`: the unique ID of the process, string
    * Field `Name`: the name of the process, string
    * Field `Category`:
    * Field `Description`:
    * Field `Version`:
    * Field `Last change`:
    * Field `Tags`:
  * Section `Time`
   * Field `Valid from`:
   * Field `Valid until`:
   * Field `Description`:
  * Section `Geography`
    * Field `Location`:
    * Field `Description`:
  * Section `Technology`
    * Field `Description`:
  * Section `Data quality`
    * Field `Flow schema`:
    * Field `Data quality entry`:
    * Field `Process schema`:
    * Field `Social schema`:

* Sheet `Documentation`
  * Section `LCI method`
    * Field `Process type`:
    * Field `LCI method`:
    * Field `Modeling constants`:
  * Section `Data source information`
    * Field `Data completeness`:
    * Field `Data selection`:
    * Field `Data treatment`:
    * Field `Data collection period`:
    * Field `Sampling procedure`:
    * Field `Use advice`:
  * Section `Completeness`: flow completeness aspects
    * each row under this section contains the aspect in the first column and
      the completeness value in the second column
  * Section `Sources`
  * Section `Administrative information`
    * Field `Intended application`:
    * Field `Data set owner`:
    * Field `Data set generator`:
    * Field `Data set documentor`:
    * Field `Publication`:
    * Field `Access and use restrictions`:
    * Field `Project`:
    * Field `Creation date`:
    * Field `Copyright`: `true`, if there is a copyright on the data set

* Sheet `Inputs | Outputs`: inputs and outputs of the process
  * Field `Is reference?`: `true | yes | x`, if this is the reference flow of the process
  * Field `Flow`:
  * Field `Category`:
  * Field `Amount`:
  * Field `Unit`:
  * Field `Costs/Revenues`:
  * Field `Currency`:
  * Field `Uncertainty`:
  * Field `(G)Mean | Mode`:
  * Field `SD | GSD`:
  * Field `Minimum`:
  * Field `Maximum`:
  * Field `Is avoided?`:
  * Field `Provider`:
  * Field `Data quality entry`:
  * Field `Location`:
  * Field `Description`:

* Sheet `Allocation`
  * Field `Default allocation method`: default allocation method as field-value
    pair: possible values are `none`, `physical`, `economic`, `causal`
  * Section `Physical & economic allocation`
    * Field `Product`: name of the co-/product
    * Field `Category`: category of the co-/product
    * Field `Physical`: physical allocation factor of that co-/product
    * Field `Economic`: economic allocation factor of that co-/product
  * Section `Causal allocation`
    * Field `Flow`: the name of the allocated flows
    * Field `Category`: the category of the allocated flow
    * Field `Direction`: the direction of the flow in the process
    * Field `Amount`: amount of the allocated flow in the process (just for information)
    * product column `i`: the respective allocation factors for the flow
      related to the co-/product `i`

* Sheet `Parameters`
  * Section `Global input parameters`
    * Field `Name`: the parameter name (must be a valid name that can appear in
      formulas)
    * Field `Value`: the parameter value
    * Field `Uncertainty`: uncertainty distribution of the parameter; same
      format as for inputs and outputs above
    * Field `(G)Mean | Mode`
    * Field `SD | GSD`
    * Field `Minimum`
    * Field `Maximum`
    * Field `Description`
  * Section `Global calculated parameters`
    * Field `Name`
    * Field `Formula`:
    * Field `Description`
  * Section `Input parameters`: input parameters of the process; same fields as
    for global input parameters
  * Section `Calculated parameters`: calculated / dependent parameters of the
    process; same fields as for global calculated parameters above

* Sheet `Flows`
  * Field `UUID`
  * Field `Name`
  * Field `Category`
  * Field `Description`
  * Field `Version`
  * Field `Last change`
  * Field `Tags`
  * Field `Type`:
  * Field `CAS`:
  * Field `Formula`:
  * Field `Location`:
  * Field `Reference flow property`:

* Sheet `Flow properties`
  * Field `UUID`
  * Field `Name`
  * Field `Category`
  * Field `Description`
  * Field `Version`
  * Field `Last change`
  * Field `Tags`
  * Field `Type`:
  * Field `Unit group`:

* Sheet `Flow property factors`: this sheet is only required when there are
  multiple flow properties of a flow
  * Field `Flow`
  * Field `Category`
  * Field `Flow property`
  * Field `Conversion factor`:

* Sheet `Unit groups`
  * Field `UUID`
  * Field `Name`
  * Field `Category`
  * Field `Description`
  * Field `Version`
  * Field `Last change`
  * Field `Tags`
  * Field `Reference unit`:
  * Field `Default flow property`:

* Sheet `Units`
  * Field `UUID`
  * Field `Name`
  * Field `Description`
  * Field `Unit group`:
  * Field `Synonyms`:
  * Field `Conversion factor`:

* Sheet `Locations`
  * Field `UUID`
  * Field `Name`
  * Field `Category`
  * Field `Description`
  * Field `Version`
  * Field `Last change`
  * Field `Tags`
  * Field `Code`:
  * Field `Latitude`:
  * Field `Longitude`:

* Sheet `Actors`
  * Field `UUID`
  * Field `Name`
  * Field `Category`
  * Field `Description`
  * Field `Version`
  * Field `Last change`
  * Field `Tags`
  * Field `Address`:
  * Field `City`:
  * Field `Zip code`:
  * Field `Country`:
  * Field `E-Mail`:
  * Field `Telefax`:
  * Field `Telephone`:
  * Field `Website`:

* Sheet `Sources`
  * Field `UUID`
  * Field `Name`
  * Field `Category`
  * Field `Description`
  * Field `Version`
  * Field `Last change`
  * Field `Tags`
  * Field `Text reference`:
  * Field `URL`:
  * Field `Year`:

* Sheet `Providers`
  * Field `UUID`
  * Field `Name`
  * Field `Category`
  * Field `Location`
