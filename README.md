# fuzzy-logic-dosage-system
Java program implementing a fuzzy logic system that calculates drug dosage based on patient temperature. Supports two fuzzy sets and two inference methods (MIN and PRODUCT). Users choose fuzzy sets and method during runtime.
This is a Java implementation of a **Fuzzy Logic Controller** that calculates the appropriate drug dosage (D) for a patient based on their body temperature (T).

# Overview
The system applies two fuzzy rules:

- **K1**: If Temperature is HIGH → Dosage is HIGH
- **K2**: If Temperature is LOW → Dosage is LOW

Two methods are available:
- **MIN method**
- **PRODUCT method**

# Repository Contents
fuzzy-logic-dosage-system
- MyDemo.java → Java source code for the fuzzy logic engine
- sets1.txt → First fuzzy set definitions
- sets2.txt → Alternative fuzzy sets
- README.md → This documentation file

# How It Works

## Fuzzy Sets
Fuzzy sets are read from one of two files, `sets1.txt` or `sets2.txt`. Each file contains membership values for:
- `TLOW` and `THIGH` (temperature)
- `DLOW` and `DHIGH` (dosage)

Example (from `sets1.txt`):

TLOW = { 0.2/37, 1/37.5, 0.5/38, 0.2/38.5, 0/39, 0/39.5, 0/40 }

THIGH = { 0/37, 0/37.5, 0.2/38, 0.5/38.5, 0.8/39, 1/39.5, 1/40 }

DLOW = { 1/0, 0.8/2, 0.5/5, 0.2/8, 0/10 }

DHIGH = { 0/0, 0.2/2, 0.5/5, 0.8/8, 1/10 }

---

## Methods
You can choose between:
- **MIN method**: Uses the minimum of the rule strength and output membership value.
- **PRODUCT method**: Uses the product of the rule strength and output membership value.
- Or **both**, for comparison.

The system uses **defuzzification** via:
- **Centroid method** (for PRODUCT)
- **Average of maxima** (for MIN)

---

## Running the Program

Example Interaction

Choose fuzzy set file:
Type 1 for sets1.txt
Type 2 for sets2.txt
> 1

Enter temperature:
> 38.5

Choose method:
Press 1 for MIN, 2 for PRODUCT, or 3 for both:
> 3

The system will display intermediate fuzzy logic steps, inference results, and the final dosage calculation(s).
