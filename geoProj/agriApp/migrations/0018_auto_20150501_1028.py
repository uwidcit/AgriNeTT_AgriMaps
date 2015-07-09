# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('agriApp', '0017_soilsandrainfall'),
    ]

    operations = [
        migrations.AlterField(
            model_name='soilsandrainfall',
            name='domsoil',
            field=models.CharField(max_length=25),
            preserve_default=True,
        ),
    ]
