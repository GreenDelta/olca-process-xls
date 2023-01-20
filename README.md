# openLCA Excel format for process data sets

This repository contains the openLCA import and export logic for process data
sets stored in Excel files.

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

* Sheet `Actors`
* Sheet `Administrative information`
* Sheet `Allocation`
* Sheet `Flow properties`
* Sheet `Flow property factors`
* Sheet `Flows`
* Sheet `General information`
* Sheet `Inputs`
* Sheet `Locations`
* Sheet `Modeling and validation`
* Sheet `Outputs`
* Sheet `Parameters`
* Sheet `Providers`
* Sheet `Sources`
* Sheet `Unit groups`
* Sheet `Units`

  * Section `Administrative information`
  * Section `Calculated parameters`
  * Section `Causal allocation`
  * Section `Data quality`
  * Section `Data source information`
  * Section `General information`
  * Section `Geography`
  * Section `Global calculated parameters`
  * Section `Global input parameters`
  * Section `Input parameters`
  * Section `Modeling and validation`
  * Section `Physical & economic allocation`
  * Section `Process evaluation and validation`
  * Section `Sources`
  * Section `Technology`
  * Section `Time`

  * Field `Address`:
  * Field `Amount`:
  * Field `CAS`:
  * Field `Category`:
  * Field `City`:
  * Field `Code`:
  * Field `Conversion factor`:
  * Field `Costs/Revenues`:
  * Field `Country`:
  * Field `Currency`:
  * Field `Data quality entry`:
  * Field `Default flow property`:
  * Field `Description`:
  * Field `E-Mail`:
  * Field `Flow`:
  * Field `Flow property`:
  * Field `Formula`:
  * Field `Is reference?`:
  * Field `Is avoided?`:
  * Field `Last change`:
  * Field `Latitude`:
  * Field `Location`:
  * Field `Longitude`:
  * Field `Maximum`:
  * Field `(G)Mean | Mode`:
  * Field `Minimum`:
  * Field `Name`:
  * Field `Provider`:
  * Field `Reference flow property`:
  * Field `Reference unit`:
  * Field `SD | GSD`:
  * Field `Synonyms`:
  * Field `Tags`:
  * Field `Telefax`:
  * Field `Telephone`:
  * Field `Text reference`:
  * Field `Type`:
  * Field `Uncertainty`:
  * Field `Unit`:
  * Field `Unit group`:
  * Field `URL`:
  * Field `UUID`:
  * Field `Version`:
  * Field `Website`:
  * Field `Year`:
  * Field `Zip code`:
  * Field `Flow schema`:
  * Field `Process schema`:
  * Field `Social schema`:
  * Field `Valid from`:
  * Field `Valid until`:
  * Field `Access and use restrictions`:
  * Field `Copyright`:
  * Field `Creation date`:
  * Field `Data set documentor`:
  * Field `Data set generator`:
  * Field `Data set owner`:
  * Field `Intended application`:
  * Field `Project`:
  * Field `Publication`:
  * Field `Data collection period`:
  * Field `Data completeness`:
  * Field `Data selection`:
  * Field `Data treatment`:
  * Field `LCI method`:
  * Field `Modeling constants`:
  * Field `Process type`:
  * Field `Review details`:
  * Field `Reviewer`:
  * Field `Sampling procedure`:
  * Field `Default allocation method`:
  * Field `Economic`:
  * Field `Physical`:
  * Field `Product`:
  * Field `Direction`:
  * Field `Value`:

* Sheet `General information`:
  * Section `General information`
    * Field `UUID`: the unique ID of the process, string
    * Field `Name`: the name of the process, string
    * Field
