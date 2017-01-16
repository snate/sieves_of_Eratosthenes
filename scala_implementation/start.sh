#!/bin/bash
(cd soe-endpoint; sbt run > log) &
(cd soe; sbt run > log) &
