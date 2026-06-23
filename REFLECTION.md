# Library Borrowing System V3

**Subject:** Fundamentals of Application Development  
**Branch:** `Review and Strengthen your MVC + SQLite`  
**Student:** Rama Walta Alinta Elsaprike
**Student ID :** 25523085  
**Date:** 2026-06-18  

---

## What you Found ?

Initially, I had populated all the tables, modified some of the data within them, and adjusted my code to align with yours, Sir. However, once everything was filled, I discovered a discrepancy between the main data and the tables, indicating that the main application and the database were completely out of sync.

This occurred because I utilized the base file from our very first meeting rather than directly forking your updated repository from GitHub. I ended up developing and cobbling together code from previous sessions, integrating your newest additions, and heavily tweaking various parts of the codebase. Consequently, my current build has significantly diverged and looks vastly different from your latest version.

---

## What you Fixed ?

Consequently, I began repairing the system starting with the Model, the View, and subsequently the Controller. This process involved incorporating code segments that were absent in my older files but present in your latest version. Additionally, I synchronized and modified specific data within the previous files, such as the errorcases code, and adjusted various formats to ensure that the red error indicators were successfully resolved into clean code :)

And now, the database and the tables are perfectly synchronized and functioning flawlessly with the main application. Consequently, if we add a new member, that new member will immediately be displayed within the tables. This seamless integration equally applies to the borrow, return, and borrow record functionalities, among others.

---

## What is still not working if anything ?

ٱلْحَمْدُ لِلَّٰهِ 

Sir, All the code runs perfectly :>

(Except, Reservation and Fines because, I saw in your latest coding that reservations and fines will be done on the next meeting; (Reservations — coming in Meeting 2.) & (Fines — coming in Meeting 2.) and in your command you also confirm that "You will review your OWN existing project against the MVC + SQL rules you have already learned, fix any issues you find, and write a short reflection on what you discovered. No new feature needs to be added — the goal is a clean, correct foundation before JavaFX is introduced) so because of that i didnt create the reservation and fines.

---

## Why layer separation matters ?

Separating layers is important because it keeps the software easy to maintain in the long term. When system parts are tangled together, a small change in the database code can easily break the user interface. By dividing the code into distinct layers, like Model View Controller, we can test pieces individually and modify the database without touching the visual presentation. It also allows multiple developers to work on the project at the same time without causing conflicts. In Simple terms, this makes the whole system much stronger and easier to manage.
