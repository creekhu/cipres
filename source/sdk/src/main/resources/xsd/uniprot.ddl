-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.0.27-standard


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema uniprot
--

CREATE DATABASE IF NOT EXISTS uniprot;
USE uniprot;
CREATE TABLE  `uniprot`.`citationtype` (
  `title` varchar(255) default NULL,
  `editorList` int(11) default NULL,
  `authorList` int(11) default NULL,
  `locator` varchar(255) default NULL,
  `dbReference` int(11) default NULL,
  `citingCitation` int(11) default NULL,
  `city` varchar(255) default NULL,
  `country` varchar(255) default NULL,
  `date` varchar(255) default NULL,
  `db` varchar(255) default NULL,
  `first` varchar(255) default NULL,
  `institute` varchar(255) default NULL,
  `last` varchar(255) default NULL,
  `name` varchar(255) default NULL,
  `number` varchar(255) default NULL,
  `publisher` varchar(255) default NULL,
  `type` varchar(255) default NULL,
  `volume` varchar(255) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  `NameListTypeID` int(11) NOT NULL,
  `NameListTypeID2` int(11) NOT NULL,
  `CitationTypeID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FKCitationTy710874` (`NameListTypeID`),
  KEY `FKCitationTy376743` (`NameListTypeID2`),
  KEY `FKCitationTy759298` (`CitationTypeID`),
  CONSTRAINT `FKCitationTy759298` FOREIGN KEY (`CitationTypeID`) REFERENCES `citationtype` (`ID`),
  CONSTRAINT `FKCitationTy376743` FOREIGN KEY (`NameListTypeID2`) REFERENCES `namelisttype` (`ID`),
  CONSTRAINT `FKCitationTy710874` FOREIGN KEY (`NameListTypeID`) REFERENCES `namelisttype` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`commenttype` (
  `text` varchar(255) default NULL,
  `absorption` int(11) default NULL,
  `kinetics` int(11) default NULL,
  `phDependence` varchar(255) default NULL,
  `redoxPotential` varchar(255) default NULL,
  `temperatureDependence` varchar(255) default NULL,
  `link` int(11) default NULL,
  `location` int(11) default NULL,
  `event` int(11) default NULL,
  `comment` varchar(255) default NULL,
  `isoform` int(11) default NULL,
  `interactant` int(11) default NULL,
  `organismsDiffer` tinyint(1) default NULL,
  `experiments` int(11) default NULL,
  `note` varchar(255) default NULL,
  `error` varchar(255) default NULL,
  `evidence` varchar(255) default NULL,
  `locationType` varchar(255) default NULL,
  `mass` float default NULL,
  `method` varchar(255) default NULL,
  `name` varchar(255) default NULL,
  `status` varchar(255) default NULL,
  `type` varchar(255) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  `EntryID` int(11) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `FKCommentTyp334668` (`EntryID`),
  CONSTRAINT `FKCommentTyp334668` FOREIGN KEY (`EntryID`) REFERENCES `entry` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`consortiumtype` (
  `name` varchar(255) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`dbreferencetype` (
  `property` int(11) default NULL,
  `evidence` varchar(255) default NULL,
  `id` varchar(255) default NULL,
  `key` varchar(255) default NULL,
  `type` varchar(255) default NULL,
  `DbReferenceTypeID` int(11) NOT NULL auto_increment,
  `CitationTypeID` int(11) default NULL,
  `EntryID` int(11) default NULL,
  `OrganismTypeID` int(11) default NULL,
  PRIMARY KEY  (`DbReferenceTypeID`),
  KEY `FKDbReferenc20622` (`CitationTypeID`),
  KEY `FKDbReferenc668292` (`EntryID`),
  KEY `FKDbReferenc74447` (`OrganismTypeID`),
  CONSTRAINT `FKDbReferenc74447` FOREIGN KEY (`OrganismTypeID`) REFERENCES `organismtype` (`ID`),
  CONSTRAINT `FKDbReferenc20622` FOREIGN KEY (`CitationTypeID`) REFERENCES `citationtype` (`ID`),
  CONSTRAINT `FKDbReferenc668292` FOREIGN KEY (`EntryID`) REFERENCES `entry` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`entry` (
  `accession` int(11) default NULL,
  `name` int(11) default NULL,
  `protein` int(11) default NULL,
  `gene` int(11) default NULL,
  `organism` int(11) default NULL,
  `organismHost` int(11) default NULL,
  `geneLocation` int(11) default NULL,
  `reference` int(11) default NULL,
  `comment` int(11) default NULL,
  `dbReference` int(11) default NULL,
  `keyword` int(11) default NULL,
  `feature` int(11) default NULL,
  `evidence` int(11) default NULL,
  `sequence` int(11) default NULL,
  `created` int(11) default NULL,
  `dataset` varchar(255) default NULL,
  `modified` int(11) default NULL,
  `version` int(11) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  `ProteinTypeID` int(11) NOT NULL,
  `SequenceTypeID` int(11) NOT NULL,
  `UniprotID` int(11) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `FKEntry730605` (`ProteinTypeID`),
  KEY `FKEntry280567` (`SequenceTypeID`),
  KEY `FKEntry248977` (`UniprotID`),
  CONSTRAINT `FKEntry248977` FOREIGN KEY (`UniprotID`) REFERENCES `uniprot` (`ID`),
  CONSTRAINT `FKEntry280567` FOREIGN KEY (`SequenceTypeID`) REFERENCES `sequencetype` (`ID`),
  CONSTRAINT `FKEntry730605` FOREIGN KEY (`ProteinTypeID`) REFERENCES `proteintype` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`eventtype` (
  `value` varchar(255) default NULL,
  `evidence` varchar(255) default NULL,
  `namedIsoforms` int(11) default NULL,
  `type` varchar(255) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  `CommentTypeID` int(11) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `FKEventType242363` (`CommentTypeID`),
  CONSTRAINT `FKEventType242363` FOREIGN KEY (`CommentTypeID`) REFERENCES `commenttype` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`evidencetype` (
  `attribute` varchar(255) default NULL,
  `category` varchar(255) default NULL,
  `date` int(11) default NULL,
  `key` varchar(255) default NULL,
  `type` varchar(255) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  `EntryID` int(11) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `FKEvidenceTy747274` (`EntryID`),
  CONSTRAINT `FKEvidenceTy747274` FOREIGN KEY (`EntryID`) REFERENCES `entry` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`featuretype` (
  `original` varchar(255) default NULL,
  `variation` int(11) default NULL,
  `location` int(11) default NULL,
  `description` varchar(255) default NULL,
  `evidence` varchar(255) default NULL,
  `id` varchar(255) default NULL,
  `ref` varchar(255) default NULL,
  `status` varchar(255) default NULL,
  `type` varchar(255) default NULL,
  `FeatureTypeID` int(11) NOT NULL auto_increment,
  `EntryID` int(11) default NULL,
  `LocationTypeID` int(11) NOT NULL,
  PRIMARY KEY  (`FeatureTypeID`),
  KEY `FKFeatureTyp123745` (`EntryID`),
  KEY `FKFeatureTyp626660` (`LocationTypeID`),
  CONSTRAINT `FKFeatureTyp626660` FOREIGN KEY (`LocationTypeID`) REFERENCES `locationtype` (`ID`),
  CONSTRAINT `FKFeatureTyp123745` FOREIGN KEY (`EntryID`) REFERENCES `entry` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`genelocationtype` (
  `name` int(11) default NULL,
  `evidence` varchar(255) default NULL,
  `type` varchar(255) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  `EntryID` int(11) default NULL,
  `StatusTypeID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FKGeneLocati137909` (`EntryID`),
  KEY `FKGeneLocati277579` (`StatusTypeID`),
  CONSTRAINT `FKGeneLocati277579` FOREIGN KEY (`StatusTypeID`) REFERENCES `statustype` (`ID`),
  CONSTRAINT `FKGeneLocati137909` FOREIGN KEY (`EntryID`) REFERENCES `entry` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`genenametype` (
  `value` varchar(255) default NULL,
  `evidence` varchar(255) default NULL,
  `type` varchar(255) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`interactanttype` (
  `id` varchar(255) default NULL,
  `label` varchar(255) default NULL,
  `intactId` varchar(255) default NULL,
  `InteractantTypeID` int(11) NOT NULL auto_increment,
  `CommentTypeID` int(11) default NULL,
  PRIMARY KEY  (`InteractantTypeID`),
  KEY `FKInteractan752172` (`CommentTypeID`),
  CONSTRAINT `FKInteractan752172` FOREIGN KEY (`CommentTypeID`) REFERENCES `commenttype` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`isoformtype` (
  `id` int(11) default NULL,
  `name` int(11) default NULL,
  `sequence` int(11) default NULL,
  `note` int(11) default NULL,
  `IsoformTypeID` int(11) NOT NULL auto_increment,
  `CommentTypeID` int(11) default NULL,
  PRIMARY KEY  (`IsoformTypeID`),
  KEY `FKIsoformTyp879726` (`CommentTypeID`),
  CONSTRAINT `FKIsoformTyp879726` FOREIGN KEY (`CommentTypeID`) REFERENCES `commenttype` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`keywordtype` (
  `value` varchar(255) default NULL,
  `evidence` varchar(255) default NULL,
  `id` varchar(255) default NULL,
  `KeywordTypeID` int(11) NOT NULL auto_increment,
  `EntryID` int(11) default NULL,
  PRIMARY KEY  (`KeywordTypeID`),
  KEY `FKKeywordTyp551866` (`EntryID`),
  CONSTRAINT `FKKeywordTyp551866` FOREIGN KEY (`EntryID`) REFERENCES `entry` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`locationtype` (
  `begin` int(11) default NULL,
  `end` int(11) default NULL,
  `position` int(11) default NULL,
  `sequence` varchar(255) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  `CommentTypeID` int(11) default NULL,
  `PositionTypeID` int(11) NOT NULL,
  `PositionTypeID2` int(11) NOT NULL,
  `PositionTypeID3` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FKLocationTy115891` (`CommentTypeID`),
  KEY `FKLocationTy456038` (`PositionTypeID`),
  KEY `FKLocationTy398227` (`PositionTypeID2`),
  KEY `FKLocationTy398228` (`PositionTypeID3`),
  CONSTRAINT `FKLocationTy398228` FOREIGN KEY (`PositionTypeID3`) REFERENCES `positiontype` (`ID`),
  CONSTRAINT `FKLocationTy115891` FOREIGN KEY (`CommentTypeID`) REFERENCES `commenttype` (`ID`),
  CONSTRAINT `FKLocationTy398227` FOREIGN KEY (`PositionTypeID2`) REFERENCES `positiontype` (`ID`),
  CONSTRAINT `FKLocationTy456038` FOREIGN KEY (`PositionTypeID`) REFERENCES `positiontype` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`namelisttype` (
  `personOrConsortium` int(11) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`objectfactory` (
  `ID` int(11) NOT NULL auto_increment,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`organismnametype` (
  `value` varchar(255) default NULL,
  `type` varchar(255) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  `OrganismTypeID` int(11) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `FKOrganismNa698607` (`OrganismTypeID`),
  CONSTRAINT `FKOrganismNa698607` FOREIGN KEY (`OrganismTypeID`) REFERENCES `organismtype` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`organismtype` (
  `name` int(11) default NULL,
  `dbReference` int(11) default NULL,
  `lineage` int(11) default NULL,
  `key` varchar(255) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  `EntryID` int(11) default NULL,
  `EntryID2` int(11) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `FKOrganismTy195955` (`EntryID`),
  KEY `FKOrganismTy941962` (`EntryID2`),
  CONSTRAINT `FKOrganismTy941962` FOREIGN KEY (`EntryID2`) REFERENCES `entry` (`ID`),
  CONSTRAINT `FKOrganismTy195955` FOREIGN KEY (`EntryID`) REFERENCES `entry` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`persontype` (
  `name` varchar(255) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`positiontype` (
  `position` int(11) default NULL,
  `status` varchar(255) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`propertytype` (
  `type` varchar(255) default NULL,
  `value` varchar(255) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  `DbReferenceTypeDbReferenceTypeID` int(11) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `FKPropertyTy87221` (`DbReferenceTypeDbReferenceTypeID`),
  CONSTRAINT `FKPropertyTy87221` FOREIGN KEY (`DbReferenceTypeDbReferenceTypeID`) REFERENCES `dbreferencetype` (`DbReferenceTypeID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`proteinnametype` (
  `value` varchar(255) default NULL,
  `evidence` varchar(255) default NULL,
  `ref` varchar(255) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  `ProteinTypeID` int(11) default NULL,
  PRIMARY KEY  (`ID`),
  KEY `FKProteinNam962911` (`ProteinTypeID`),
  CONSTRAINT `FKProteinNam962911` FOREIGN KEY (`ProteinTypeID`) REFERENCES `proteintype` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`proteintype` (
  `name` int(11) default NULL,
  `domain` int(11) default NULL,
  `component` int(11) default NULL,
  `evidence` varchar(255) default NULL,
  `type` varchar(255) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`referencetype` (
  `citation` int(11) default NULL,
  `scope` int(11) default NULL,
  `source` int(11) default NULL,
  `evidence` varchar(255) default NULL,
  `key` varchar(255) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  `CitationTypeID` int(11) NOT NULL,
  `EntryID` int(11) default NULL,
  `SourceDataTypeID` int(11) NOT NULL,
  PRIMARY KEY  (`ID`),
  KEY `FKReferenceT151017` (`CitationTypeID`),
  KEY `FKReferenceT839931` (`EntryID`),
  KEY `FKReferenceT828156` (`SourceDataTypeID`),
  CONSTRAINT `FKReferenceT828156` FOREIGN KEY (`SourceDataTypeID`) REFERENCES `sourcedatatype` (`ID`),
  CONSTRAINT `FKReferenceT151017` FOREIGN KEY (`CitationTypeID`) REFERENCES `citationtype` (`ID`),
  CONSTRAINT `FKReferenceT839931` FOREIGN KEY (`EntryID`) REFERENCES `entry` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`sequencetype` (
  `value` varchar(255) default NULL,
  `checksum` varchar(255) default NULL,
  `length` int(11) default NULL,
  `mass` int(11) default NULL,
  `modified` int(11) default NULL,
  `version` int(11) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`sourcedatatype` (
  `speciesOrStrainOrPlasmid` int(11) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`statustype` (
  `value` varchar(255) default NULL,
  `status` varchar(255) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE  `uniprot`.`uniprot` (
  `entry` int(11) default NULL,
  `copyright` varchar(255) default NULL,
  `ID` int(11) NOT NULL auto_increment,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
