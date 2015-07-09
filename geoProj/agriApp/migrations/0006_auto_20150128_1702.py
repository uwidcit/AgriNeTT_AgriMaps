# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('agriApp', '0005_countours'),
    ]

    operations = [
        migrations.RenameModel(
            old_name='Countours',
            new_name='Contours',
        ),
    ]
